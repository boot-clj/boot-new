[![Stories in Ready](https://badge.waffle.io/seancorfield/boot-new.png?label=ready&title=Ready)](https://waffle.io/seancorfield/boot-new)
# boot-new

A Boot task that generates projects from Leiningen templates or Boot templates.

## Usage

You can specify a template and a project name:

    boot -d seancorfield/boot-new new -t template-name -n project-name

For other options, ask `new` for help:

    boot -d seancorfield/boot-new new -h

Currently all of the basic options from Leiningen's `new` task are supported, and basic versions of the built-in templates (`app`, `default`, `task`, `template`) are available..

## Boot Templates

Boot templates are very similar to Leiningen templates but have an artifact name based on `boot-template` instead of `lein-template` and `boot` instead of `leiningen` in all the namespaces names. The built-in templates for Boot new are Boot templates, that produce Boot projects.

## Roadmap

* Add "generate" task that can add new pieces to an existing project, based on a template.

## License

Copyright © 2016 Sean Corfield and the Leiningen Team for much of the code -- thank you!

Distributed under the Eclipse Public License version 1.0.
