(set-env! :resource-paths #{"src"})

(def version "0.5.3")

(task-options!
 pom {:project     'boot/new
      :version     version
      :description "Generate projects from Boot templates."
      :url         "https://github.com/boot-clj/boot-new"
      :scm         {:url "https://github.com/boot-clj/boot-new"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build []
  (comp (pom) (jar) (install)))

(deftask deploy
  []
  (comp (pom) (jar) (push)))
