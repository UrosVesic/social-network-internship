package com.levi9.internship.social.network.service.defaultservice;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.dto.UserSignInRequest;
import com.levi9.internship.social.network.dto.UserSignInResponse;
import com.levi9.internship.social.network.dto.UserSignUpRequest;
import com.levi9.internship.social.network.dto.CustomForgotPasswordRequest;
import com.levi9.internship.social.network.dto.CustomResetPasswordRequest;
import com.levi9.internship.social.network.exceptions.BusinessException;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import com.levi9.internship.social.network.exceptions.IAMProviderException;
import com.levi9.internship.social.network.model.User;
import com.levi9.internship.social.network.service.AuthService;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Log4j2
@Service
public class CognitoAuthService implements AuthService {
    @Value("${aws.cognito.clientId}")
    private String clientId;
    @Value("${aws.cognito.clientSecret}")
    private String clientSecret;
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    private final UserDao userDao;

    public CognitoAuthService(CognitoIdentityProviderClient cognitoIdentityProviderClient,
                              final UserDao userDao) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
        this.userDao = userDao;

    }

    @Override
    public void signUp(final UserSignUpRequest request) throws IAMProviderException {
        try {
            log.info("Processing sign up request");
            final List<AttributeType> attributeTypes = request.getAttributes().entrySet()
                    .stream()
                    .map(entry -> AttributeType.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .build()).toList();
            final SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .secretHash(CognitoAuthUtil
                            .calculateSecretHash(clientId, clientSecret, request.getUsername()))
                    .userAttributes(attributeTypes)
                    .build();

            final SignUpResponse signUpResponse = cognitoIdentityProviderClient.signUp(signUpRequest);
            if (signUpResponse != null) {
                log.info("User confirmed: {}", signUpResponse.userConfirmed().toString());
            }
        } catch (final Exception ex) {
            throw new IAMProviderException(ErrorCode.ERROR_DURING_SIGN_UP, ex.getMessage());
        }
    }


    @Override
    public UserSignInResponse signIn(final UserSignInRequest request) throws IAMProviderException {
        try {
            log.info("Processing sign in request");
            final Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", request.getUsername());
            authParams.put("PASSWORD", request.getPassword());
            authParams.put(
                    "SECRET_HASH",
                    CognitoAuthUtil.calculateSecretHash(clientId, clientSecret, request.getUsername()));
            authParams.put("SRP_A", CognitoAuthUtil.getA().toString(16));
            final InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(authParams)
                    .clientId(clientId)
                    .build();
            final InitiateAuthResponse authResponse = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);

            if (authResponse != null
                    && authResponse.challengeName() != null
                    && authResponse.challengeNameAsString() != null) {
                throw new IAMProviderException(ErrorCode.ERROR_DURING_SIGN_IN, authResponse.challengeNameAsString());
            }
            if (authResponse != null && authResponse.authenticationResult() != null) {
                final UserSignInResponse userSignInResponseData = new UserSignInResponse();
                userSignInResponseData.setAccessToken(authResponse.authenticationResult().accessToken());
                userSignInResponseData.setExpiresIn(authResponse.authenticationResult().expiresIn());
                userSignInResponseData.setIdToken(authResponse.authenticationResult().idToken());
                userSignInResponseData.setRefreshToken(authResponse.authenticationResult().refreshToken());
                userSignInResponseData.setTokenType(authResponse.authenticationResult().tokenType());

                final DecodedJWT decodedIdToken = JWT.decode(userSignInResponseData.getIdToken());
                final String userId = decodedIdToken.getSubject();
                //store user in db if it does not already exist
                if (userDao.findById(userId).isEmpty()) {
                    final User user = new User();
                    user.setId(userId);
                    user.setEmail(decodedIdToken.getClaim("email").asString());
                    user.setUsername(decodedIdToken.getClaim("cognito:username").asString());
                    userDao.save(user);
                }
                return userSignInResponseData;
            }
            return null;
        } catch (final Exception ex) {
            throw new IAMProviderException(ErrorCode.ERROR_DURING_SIGN_IN, ex.getMessage());
        }
    }

    //this method is when user want to sign out, execution is successful but token is not invalid
    @Override
    public void signOut(final String token) throws IAMProviderException {
        try {
            log.info("Processing sign out request");
            final GlobalSignOutRequest globalSignOutRequest = GlobalSignOutRequest.builder()
                    .accessToken(token).build();
            cognitoIdentityProviderClient.globalSignOut(globalSignOutRequest);
            log.info("Sign out successful");
        } catch (final Exception ex) {
            throw new IAMProviderException(ErrorCode.ERROR_DURING_SIGN_OUT, ex.getMessage());
        }
    }

    @Override
    public User getCurrentUser() {
        final String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findById(userId).orElseThrow(() -> new BusinessException("User %s does not exist".formatted(userId)));
    }


    @Override
    public void forgotPassword(final CustomForgotPasswordRequest request) throws IAMProviderException {
        try {
            final User user =
                    userDao.findByUsername(request.getUsername()).orElseThrow(() -> new BusinessException("User does not exist"));
            if (!user.getEmail().equals(request.getEmail())) {
                throw new BusinessException(ErrorCode.ERROR_SENDING_CONFIRMATION_CODE,
                        String.format("Provided email address does not match with %s's email address", request.getUsername()));
            }
            final ForgotPasswordRequest forgotPasswordRequest = ForgotPasswordRequest.builder()
                    .secretHash(CognitoAuthUtil
                            .calculateSecretHash(clientId, clientSecret, request.getUsername()))
                    .clientId(clientId)
                    .username(request.getUsername())
                    .build();
            cognitoIdentityProviderClient.forgotPassword(forgotPasswordRequest);
        } catch (final Exception ex) {
            throw new IAMProviderException(ErrorCode.ERROR_SENDING_CONFIRMATION_CODE, ex.getMessage());
        }
    }

    @Override
    public void resetPassword(final CustomResetPasswordRequest request) throws IAMProviderException {
        try {
            final ConfirmForgotPasswordRequest confirmForgotPasswordRequest = ConfirmForgotPasswordRequest.builder()
                    .secretHash(CognitoAuthUtil
                            .calculateSecretHash(clientId, clientSecret, request.username()))
                    .clientId(clientId)
                    .username(request.username())
                    .confirmationCode(request.confirmationCode())
                    .password(request.password())
                    .build();
            cognitoIdentityProviderClient.confirmForgotPassword(confirmForgotPasswordRequest);
        } catch (final Exception ex) {
            throw new IAMProviderException(ErrorCode.ERROR_DURING_PASSWORD_RESET, ex.getMessage());
        }
    }

    private class CognitoAuthUtil {
        private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        private static final String HEX_N = """
                FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1\
                29024E088A67CC74020BBEA63B139B22514A08798E3404DD\
                EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245\
                E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED\
                EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D\
                C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F\
                83655D23DCA3AD961C62F356208552BB9ED529077096966D\
                670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B\
                E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9\
                DE2BCBF6955817183995497CEA956AE515D2261898FA0510\
                15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64\
                ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7\
                ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B\
                F12FFA06D98A0864D87602733EC86A64521F2B18177B200C\
                BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31\
                43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF\
                """;

        public static String calculateSecretHash(final String userClientId, final String userSecret, final String userName)
                throws NoSuchAlgorithmException, InvalidKeyException {
            if (userSecret == null) {
                return null;
            }
            final SecretKeySpec signingKey = new SecretKeySpec(
                    userSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA256_ALGORITHM);

            final Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(mac.doFinal(userClientId.getBytes(StandardCharsets.UTF_8)));
        }

        public static BigInteger getA() throws NoSuchAlgorithmException {
            BigInteger A;
            BigInteger a;
            do {
                a = new BigInteger(1024, SecureRandom.getInstance("SHA1PRNG"))
                        .mod(new BigInteger(HEX_N, 16));
                A = BigInteger.valueOf(2)
                        .modPow(a, new BigInteger(HEX_N, 16));
            } while (A.mod(new BigInteger(HEX_N, 16)).equals(BigInteger.ZERO));
            return A;
        }
    }
}
