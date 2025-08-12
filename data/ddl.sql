alter table bill
    add delete boolean default false;

alter table bill
    add delete_by varchar;

alter table bill
    add delete_time timestamp;

alter table bill
    add comments varchar(200);



create table bill
(
    id             serial
        primary key,
    created_date   timestamp      default CURRENT_TIMESTAMP                   not null,
    amount         numeric(12, 2)                                             not null,
    vat_amt        numeric(12, 2) default 0,
    discount_amt   numeric(12, 2) default 0,
    total          numeric(12, 2)                                             not null,
    description    text,
    invoice_no     integer        default nextval('invoice_no_seq'::regclass) not null,
    payment_method varchar(20)    default 'Cash'::character varying           not null,
    created_by     varchar                                                    not null
);

alter table bill
    owner to postgres;

create table bill_details
(
    id             serial
        primary key,
    food           text           not null,
    size           varchar(50),
    quantity       integer        not null,
    per_unit_price numeric(12, 2) not null,
    total_price    numeric(12, 2) not null,
    bill_id        integer        not null
        references bill
            on delete cascade
);

alter table bill_details
    owner to postgres;

create table category
(
    id          serial
        primary key,
    name        text not null
        unique,
    description text,
    status      boolean default true,
    deleted     boolean default false
);

alter table category
    owner to postgres;

create table item
(
    id           serial
        primary key,
    item_name    text           not null,
    description  text,
    quantity     varchar(50),
    price        numeric(12, 2) not null,
    discount     numeric(5, 2) default 0,
    vat          numeric(5, 2) default 0,
    available    boolean       default true,
    cat_id       integer        not null
        references category
            on delete cascade,
    created_date timestamp,
    deleted      boolean       default false
);

alter table item
    owner to postgres;

create table keyvalue
(
    id                serial
        primary key,
    report_path       text,
    logo              text,
    vat               double precision,
    discount          double precision,
    duration_count    text,
    subscription_from text
);

alter table keyvalue
    owner to postgres;

create table food_size
(
    id   serial
        primary key,
    name varchar(50) not null
);

alter table food_size
    owner to postgres;

create table role
(
    id          integer default nextval('role_id_seq'::regclass) not null
        primary key,
    name        varchar(50)                                      not null
        unique,
    description text
);

alter table role
    owner to postgres;

create table app_user
(
    id         integer   default nextval('app_user_id_seq'::regclass) not null
        primary key,
    username   varchar(50)                                            not null
        unique,
    password   varchar(255)                                           not null,
    mobile     varchar(100)
        constraint app_user_email_key
            unique,
    full_name  varchar(100),
    is_active  boolean   default true,
    created_at timestamp default CURRENT_TIMESTAMP,
    role_id    integer
                                                                      references role
                                                                          on delete set null
);

alter table app_user
    owner to postgres;

