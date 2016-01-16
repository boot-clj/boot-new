(set-env! :resource-paths #{"src"}
          :dependencies '[[bultitude "0.2.8"]
                          [leiningen-core "2.5.3"]
                          [stencil "0.5.0"]])

(def version "0.1.0-SNAPSHOT")

(task-options!
 pom {:project     'seancorfield/boot-new
      :version     version
      :description "Generate projects from Boot templates."
      :url         "https://github.com/seancorfield/boot-new"
      :scm         {:url "https://github.com/seancorfield/boot-new"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build []
  (comp (pom) (jar) (install)))

(deftask deploy
  [g gpg-sign bool "Sign jar using GPG private key."]
  (comp (pom) (jar) (apply push (mapcat identity *opts*))))


(deftask new
  "Generate a new project from a template."
  [t template TEMPLATE str "the template to use"
   n name     NAME     str "the name of the project to generate"]
  (require 'boot.new)
  ((resolve 'boot.new/new) {} template name))
