(ns gay.object.caduceus.component)

(defn translatable [key & args]
  (net.minecraft.network.chat.Component/translatable key (object-array args)))

(defn translatable-with-fallback [key fallback]
  (net.minecraft.network.chat.Component/translatableWithFallback key fallback))

(defn join [separator coll]
  (reduce
    #(-> %1
         (.append separator)
         (.append %2))
    (.copy (first coll))
    (rest coll)))

(defn red [component]
  (.withStyle component net.minecraft.ChatFormatting/RED))
