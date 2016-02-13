(set-env! :resource-paths #{"src"}
          :dependencies '[[stencil "0.5.0"
                           :exclusions [org.clojure/clojure]]])

(def version "0.4.0")

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
  []
  (comp (pom) (jar) (push)))
