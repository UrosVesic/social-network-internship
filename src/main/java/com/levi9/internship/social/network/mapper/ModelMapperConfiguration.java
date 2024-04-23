package com.levi9.internship.social.network.mapper;

import com.levi9.internship.social.network.dto.EventRespondResponse;
import com.levi9.internship.social.network.dto.FriendResponse;
import com.levi9.internship.social.network.dto.GroupResponse;
import com.levi9.internship.social.network.dto.PostResponse;
import com.levi9.internship.social.network.model.EventRespond;
import com.levi9.internship.social.network.model.Group;
import com.levi9.internship.social.network.model.Post;
import com.levi9.internship.social.network.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        //this is place for mappings
        modelMapper.createTypeMap(Post.class, PostResponse.class)
                .addMappings(mapper ->
                        mapper.map(source -> source.getCreatedBy().getId(), PostResponse::setCreatedBy))
                .addMappings(mapper ->
                        mapper.map(source -> source.getGroup().getId(), PostResponse::setGroupId));

        modelMapper.createTypeMap(User.class, FriendResponse.class)
                .addMappings(mapper ->
                        mapper.map(User::getEmail, FriendResponse::setEmail))
                .addMappings(mapper ->
                        mapper.map(User::getUsername, FriendResponse::setUsername));

        modelMapper.createTypeMap(Group.class, GroupResponse.class)
                .addMappings(mapper ->
                        mapper.map(source -> source.getAdmin().getUsername(), GroupResponse::setAdmin))
                .addMappings(mapper -> mapper.map(Group::isPrivate, GroupResponse::setNonPublic));

        modelMapper.createTypeMap(EventRespond.class, EventRespondResponse.class)
                .addMappings(mapper ->
                        mapper.map(EventRespond::getEvent, EventRespondResponse::setEventResponse));

        return modelMapper;
    }
}
