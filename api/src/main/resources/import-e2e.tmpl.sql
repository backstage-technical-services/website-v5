insert into
    users(identity_id, username, email, first_name, last_name, group_id)
values
    ('${AUTH0_ID}', 'cypress', '${AUTH0_USERNAME}', 'Test', 'User', '${AUTH0_ROLE_ID}');

insert into quotes
    (culprit, quote, date, added_by_id, rating)
values
    ('Mitch Hedberg', 'I''m sick of following my dreams, man. I''m just going to ask where they''re going and hook up with ’em later.', '2021-11-23 16:36:01.000000', 1, 0),
    ('Jack Handey', 'Before you criticize someone, you should walk a mile in their shoes. That way when you criticize them, you are a mile away from them and you have their shoes.', '2022-09-23 21:50:42.000000', 1, 0),
    ('Bob and Peter', E'Bob: "Looks like you''ve been missing a lot of work lately."\nPeter: "I wouldn''t say I''ve been missing it, Bob."', '2023-11-01 19:40:12.000000', 1, 0),
    ('Will Ferrell', 'Before you marry a person, you should first make them use a computer with slow Internet to see who they really are.', '2020-04-14 01:28:47.000000', 1, 0),
    ('Rita Rudner', 'I love being married. It''s so great to find that one special person you want to annoy for the rest of your life.', '2019-04-09 19:15:48.000000', 1, 0),
    ('Phil Connors', 'Ned, I would love to stand here and talk with you—but I’m not going to.', '2019-03-29 18:22:03.000000', 1, 0),
    ('Erma Bombeck', 'When your mother asks, ‘Do you want a piece of advice?’ it is a mere formality. It doesn’t matter if you answer yes or no. You’re going to get it anyway.', '2019-01-15 11:08:32.000000', 1, 0),
    ('Anonymous', 'Insomnia sharpens your math skills because you spend all night calculating how much sleep you’ll get if you’re able to ''fall asleep right now.''', '2021-11-12 10:42:49.000000', 1, 0),
    ('Rodney Dangerfield', 'I haven''t spoken to my wife in years. I didn''t want to interrupt her.', '2021-01-03 14:52:57.000000', 1, 0),
    ('Ted Striker and Dr. Rumack', E'Ted Striker: "Surely you can’t be serious."\nDr. Rumack: "I am serious. And don’t call me Shirley"', '2021-02-21 18:34:54.0000001', 1, 0),
    ('Joan Rivers', 'You know you’ve reached middle age when you’re cautioned to slow down by your doctor, instead of by the police.', '2021-09-01 14:31:43.000000', 1, 0),
    ('Sheldon Cooper', 'I’m not insane. My mother had me tested.', '2018-09-25 04:05:11.000000', 1, 0),
    ('Lucy and Fred Mertz', E'Lucy: "There''s just two things keeping me from dancing in that show."\nFred: "Your feet?"', '2020-11-15 04:09:12.000000', 1, 0),
    ('Anonymous', 'Common sense is like deodorant. The people who need it most never use it.', '2021-01-31 09:50:06.000000', 1, 0),
    ('Ace Ventura', 'If I’m not back in five minutes, just wait longer.', '2022-02-04 02:25:17.000000', 1, 0)
    ;
