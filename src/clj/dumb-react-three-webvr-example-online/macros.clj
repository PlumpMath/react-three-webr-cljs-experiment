(ns dumb-react-three-webvr-example-online.macros)

(defmacro += [obj amount & path]
   `(let [attr-val-state# (aget ~obj ~@path)
         new-val# (+ attr-val-state# ~amount)]
      (aset ~obj ~@path new-val#)))
