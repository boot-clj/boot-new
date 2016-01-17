# boot-new

A Boot task that generates projects from Leiningen templates.

## Usage

You can specify a template and a project name:

    boot -d seancorfield/boot-new:0.1.0-SNAPSHOT new -t template-name -n project-name

For other options, ask `new` for help:

    boot -d seancorfield/boot-new:0.1.0-SNAPSHOT new -h

Currently all of the basic options from Leiningen's `new` task are supported, although there are no built-in templates.

## Roadmap

* Add support for "Boot templates" -- with `boot-template` in the artifact name, instead of `lein-template`, and with `boot` in the namespace names, instead of `leiningen`.
* Add built-in Boot templates for `app`, `default`, `task`, `template`.
* Add "generate" task that can add new pieces to an existing project, based on a template.

## License

Copyright © 2016 Sean Corfield and the Leiningen Team for much of the code -- thank you!

Distributed under the Eclipse Public License version 1.0.
