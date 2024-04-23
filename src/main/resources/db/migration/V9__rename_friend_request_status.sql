-- The purpose of this script is to rename friend_request status 'APPROVED' to 'ACCEPTED
-- In order to do this, firstly we add new enum value in column definition to rename status in existing rows (if there's any)
-- When we are sure that there's no more rows with old enum value, we can again update list of enum values to remove the old one
-- If there's rows with old enum value ('APPROVED'), database will not allow modifying column definition
-- (to remove an old enum value and add a new one - the last statement in the script), because of that we first do renaming
ALTER TABLE friend_request
    MODIFY COLUMN status ENUM('APPROVED','REJECTED','PENDING','ACCEPTED');

UPDATE friend_request
SET status = 'ACCEPTED'
WHERE status = 'APPROVED';

ALTER TABLE friend_request
    MODIFY COLUMN status ENUM('ACCEPTED','REJECTED','PENDING');