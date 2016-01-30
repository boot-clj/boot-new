# boot-new

A Boot task that generates projects from Leiningen templates or Boot templates.

[![Clojars Project](https://img.shields.io/clojars/v/seancorfield/boot-new.svg)](https://clojars.org/seancorfield/boot-new)

## Getting Started

Create a basic application:

    boot -d seancorfield/boot-new new -t app -n myapp
    cd myapp
    boot run

Built-in templates are:

* `app` -- A minimal Hello World! application. Comes with `build`, `run`, `test` tasks. `build` creates a runnable "uberjar" in the `target` folder.
* `default` -- A minimal library. Comes with `build`, `test` tasks. `build` installs a JAR of the project into your local Maven cache so you can use `boot watch build` while you're developing to have the latest version always available to other projects.
* `task` -- An example set of Boot tasks. Provides `*-pass-thru`, `*-post`, `*-pre`, `*-simple` task examples for you to build on, as well as `build`, `test` tasks. `build` works as for `default` above.
* `template` -- A minimal Boot template. Provides `build`, `new`, `test` tasks. `build` works as for `default` above. `new` will create a new project based on the template itself.

## General Usage

You can specify a template and a project name:

    boot -d seancorfield/boot-new new -t template-name -n project-name

`boot-new` will look for `template-name/boot-template` (on Clojars and Maven Central). If it doesn't find a Boot Template (see below), it will look for `template-name/lein-template` instead. `boot-new` should be able to run any existing Leiningen template (if you find one that doesn't work, [please tell me about it](https://github.com/seancorfield/boot-new/issues)!). `boot-new` will then generate a new project folder called `project-name` containing files generated from the specified `template-name`.

If the folder `project-name` already exists, `boot-new` will not overwrite it unless you specify the `-f` / `--force` option. You can override the folder name used with the `-o` / `--to-dir` option. By default, `boot-new` will look for the most recent stable release of the specified template. You can tell it to search for snapshots with the `-S` / `--snapshot` option, and you can specify a particular version to use with the `-V` / `--template-version` option. In general, the long-form option follows the naming used by Leiningen's `new` task for familiarity.

You can pass arguments through to the underlying template with the `-a` / `--args` option (Leiningen uses `--` to separate template arguments from other options but Boot already parses options a little differently).

For a full list of options, ask `new` for help:

    boot -d seancorfield/boot-new new -h

The intent is that all of the basic options from Leiningen's `new` task are supported, along with Boot-specific versions of the built-in templates (`app`, `default`, `task` -- instead of Leiningen's `plugin`, and `template`).

## Boot Templates

Boot templates are very similar to Leiningen templates but have an artifact name based on `boot-template` instead of `lein-template` and `boot` instead of `leiningen` in all the namespaces names. The built-in templates for `boot-new` are Boot templates, that produce Boot projects. In particular the `boot.new.templates` namespace provides functions such as `renderer` and `->files` that are the equivalent of the ones found in `leiningen.new.templates` when writing a Leiningen Template.

## Roadmap

[![Stories in Ready](https://badge.waffle.io/seancorfield/boot-new.png?label=ready&title=Ready)](https://waffle.io/seancorfield/boot-new)

* Add "generate" task that can add new pieces to an existing project, based on a template.

## License

Copyright © 2016 Sean Corfield and the Leiningen Team for much of the code -- thank you!

Distributed under the Eclipse Public License version 1.0.
