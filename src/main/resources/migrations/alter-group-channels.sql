alter table groups_channels add "unread" int;
alter table groups_channels add "display_order" int;
alter table groups_channels add "last_message_id" bigint;
alter table groups_channels
add constraint "fk_last_message_id"
foreign key (last_message_id)
references messages (id);

alter table groups add "last_message_id" bigint;
alter table groups
add constraint "fk_last_message_id"
foreign key (last_message_id)
references messages (id);