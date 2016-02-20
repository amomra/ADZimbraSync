# Zimbra plugin for Active Directory users and groups synchronization

This plugin was develop to allow administrators to configure Zimbra to synchronize users and groups from a AD domain controller.

## Features

1. User bidirectional attributes syncronization (no lazy provision support);
2. Group synchronization as distribuition lists;
3. Users authentication;
4. Change user password on AD (supports 'Password must change' option).

## Planned features

1. Administrator console login based on group permissions.

## Installation

Work in progress.

## Remarks

This is a project I was working in my free time to help the migration of our mail solution to Zimbra. The migration dropped due time restritions and the development halted. So if you want to continue the development you can fork this project or contact me if you want some help.

Since this was an internal project all comments in code and notes are in portuguese.

## References

* Some of the code on this project was based on ADPassword and ADProvision Zimbra Extensions by Antonio Messina (https://code.google.com/p/adpassword/).

## License

This project was released under MIT license.
