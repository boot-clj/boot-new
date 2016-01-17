(ns boot.new
  "Generate project scaffolding based on a template.
  Adapted from leiningen.new, with permission of the Leiningen team."
  {:boot/export-tasks true}
  (:refer-clojure :exclude [new])
  (:require [boot.core :as boot :refer [deftask]]
            [boot.util :as util]
            [bultitude.core :as bultitude]
            [leiningen.core.classpath :as cp]
            [leiningen.core.project :as project]
            [leiningen.core.user :as user]
            [leiningen.core.main :refer [abort parse-options option-arg]]
            [leiningen.new.templates :refer [*dir* *force?*]])
  (:import java.io.FileNotFoundException))

(def ^:dynamic *use-snapshots?* false)
(def ^:dynamic *template-version* nil)

(defn- fake-project [name]
  (let [template-symbol (symbol name "lein-template")
        template-version (cond *template-version* *template-version*
                               *use-snapshots?*   "(0.0.0,)"
                               :else              "RELEASE")
        user-profiles (:user (user/profiles))
        repositories (reduce
                      (:reduce (meta project/default-repositories))
                      project/default-repositories
                      (:plugin-repositories user-profiles))]
    (merge {:templates [[template-symbol template-version]]
            :repositories repositories}
           (select-keys user-profiles [:mirrors]))))

(defn resolve-remote-template [name sym]
  (try (cp/resolve-dependencies :templates (fake-project name) :add-classpath? true)
       (require sym)
       true
       (catch clojure.lang.Compiler$CompilerException e
         (abort (str "Could not load template, failed with: " (.getMessage e))))
       (catch Exception e nil)))

(defn resolve-template [name]
  (let [sym (symbol (str "leiningen.new." name))]
    (if (try (require sym)
             true
             (catch FileNotFoundException _
               (resolve-remote-template name sym)))
      (resolve (symbol (str sym "/" name)))
      (abort "Could not find template" name "on the classpath."))))

;; A lein-newnew template is actually just a function that generates files and
;; directories. We have a bit of convention: we expect that each template is on
;; the classpath and is based in a .clj file at `leiningen/new/`. Making this
;; assumption, users can simply give us the name of the template they wish to
;; use and we can `require` it without searching the classpath for it or doing
;; other time consuming things. If this namespace isn't found and we are
;; running Leiningen 2, we can resolve it via pomegranate first.
;;
;; Since our templates are just function calls just like Leiningen tasks, we can
;; also expect that a template generation function also be named the same as the
;; last segment of its namespace. This is what we call to generate the project.
(defn create
  [template name & args]
  (cond
    (and (re-find #"(?i)(?<!(clo|compo))jure" name)
         (not (System/getenv "LEIN_IRONIC_JURE")))
    (abort "Sorry, names such as clojure or *jure are not allowed."
           "\nIf you intend to use this name ironically, please set the"
           "\nLEIN_IRONIC_JURE environment variable and try again.")
    (and (re-find #"(?i)(?<!(cl|comp))eaxure" name)
         (not (System/getenv "LEIN_IRONIC_EAXURE")))
    (abort "Sorry, names such as cleaxure or *eaxure are not allowed."
           "\nIf you intend to use this name ironically, please set the"
           "\nLEIN_IRONIC_EAXURE environment variable and try again.")
    (= name "clojure")
    (abort "Sorry, clojure can't be used as a project name."
           "\nIt will confuse Clojure compiler and cause obscure issues.")
    (and (re-find #"[A-Z]" name)
         (not (System/getenv "LEIN_BREAK_CONVENTION")))
    (abort "Project names containing uppercase letters are not recommended"
           "\nand will be rejected by repositories like Clojars and Central."
           "\nIf you're truly unable to use a lowercase name, please set the"
           "\nLEIN_BREAK_CONVENTION environment variable and try again.")
    (not (symbol? (try (read-string name) (catch Exception _))))
    (abort "Project names must be valid Clojure symbols.")
    :else (apply (resolve-template template) name args)))

;; Since we have our convention of templates always being at
;; `leiningen.new.<template>`, we can easily search the classpath
;; to find templates in the same way that Leiningen can search to
;; find tasks. Furthermore, since our templates will always have a
;; function named after the template that is the entry-point, we can
;; also expect that it has the documentation for the template. We can
;; just look up these templates on the classpath, require them, and then
;; get the metadata off of that function to list the names and docs
;; for all of the available templates.

(defn lib-list []
  (for [n (bultitude/namespaces-on-classpath :prefix "leiningen.new.")
        ;; There are things on the classpath at `leiningen.new` that we
        ;; don't care about here. We could use a regex here, but meh.
        :when (not= n 'leiningen.new.templates)]
    (-> (doto n require)
        (the-ns)
        (ns-resolve (symbol (last (.split (str n) "\\.")))))))

(defn template-show
  "Show details for a given template."
  [name]
  (let [resolved (meta (resolve-template name))]
    (println (:doc resolved "No documentation available."))
    (println)
    (println "Argument list:" (or (:help-arglists resolved)
                                  (:arglists resolved)))))

(ns-unmap *ns* 'new)

(deftask new
  "Generate a new project from a template. The long form arguments mostly
follow those of `lein new` except that -n / --name is required and you
specify the template with -t / --template."
  [a args             ARG      [str] "arguments for the template itself."
   f force                     bool  "Force Boot new to overwrite existing directory."
   n name             NAME     str   "generated project name"
   o to-dir           DIR      str   "directory to use instead of NAME"
   s show                      bool  "Show documentation for the template."
   S snapshot                  bool  "Look for a SNAPSHOT version of the template."
   t template         TEMPLATE str   "the template to use"
   V template-version VER      str   "the version of the template to use"]

  (let [template (or template "default")]

    (boot/with-pass-thru fs

      (cond show
            (template-show template)

            (not name)
            (util/exit-error (println "Project name is required (-n, --name)."))

            :else (binding [*dir*              to-dir
                            *use-snapshots?*   snapshot
                            *template-version* template-version
                            *force?*           force]
                    (apply create template name args))))))
