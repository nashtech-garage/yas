--liquibase formatted sql
--changeset bangnguyen:eventuate-dll-0006
--comment: create replication slot
DO
    '
        DECLARE
            eventuate_slot VARCHAR := ''eventuate_slot_${eventualSlotName}'';
        BEGIN
            IF NOT EXISTS (SELECT * FROM pg_replication_slots WHERE slot_name = eventuate_slot) THEN
                PERFORM pg_create_logical_replication_slot(eventuate_slot, ''wal2json'');
            END IF;
        END
    ';