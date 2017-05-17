(ns boot.new
  "Generate project scaffolding based on a template.
  Adapted from leiningen.new, with permission of the Leiningen team."
  {:boot/export-tasks true}
  (:refer-clojure :exclude [new])
  (:require [boot.core :as core]
            [boot.util :as util]))

(core/deftask new
  "Generate a new project from a template

  The long form arguments mostly follow those of `lein new` except that -n / --name
is required and you specify the template with -t / --template."
  [a args             ARG      [str] "arguments for the template itself."
   f force                     bool  "Force Boot new to overwrite existing directory."
   g generate         SPEC     [str] "things to generate"
   n name             NAME     str   "generated project name"
   o to-dir           DIR      str   "directory to use instead of NAME"
   p prefix           PATH     str   "source directory prefix for generate (src)"
   s show                      bool  "Show documentation for the template."
   S snapshot                  bool  "Look for a SNAPSHOT version of the template."
   t template         TEMPLATE str   "the template to use"
   V template-version VER      str   "the version of the template to use"
   v verbose                   int   "Be increasingly verbose."]

  ;; suppress target warning since it makes no sense for boot-new
  (System/setProperty "BOOT_EMIT_TARGET" "no")
  (core/merge-env! :dependencies '[[stencil "0.5.0" :exclusions [org.clojure/clojure org.clojure/core.cache]]])

  (core/with-pass-thru fs
    (require '[boot.new-helpers :as helpers])
    (cond (and show (not template))
          (util/exit-error (println "Template name is required (-t, --template) for show option (-s, --show)."))

          show
          ((resolve 'helpers/template-show) template)

          generate
          ((resolve 'helpers/generate-code) *opts*)

          (not name)
          (throw (ex-info "Project name is required (-n, --name)." {}))

          :else ((resolve 'helpers/create) *opts*))))
