(ns gay.object.caduceus.utils.component)

(defn literal [text]
  (net.minecraft.network.chat.Component/literal text))

(defn translatable [key & args]
  (net.minecraft.network.chat.Component/translatable key (object-array args)))

(defn translatable-with-fallback [key fallback]
  (net.minecraft.network.chat.Component/translatableWithFallback key fallback))

(defn join
  ([coll] (join "" coll))
  ([separator coll]
   (reduce
     #(-> %1
          (.append separator)
          (.append %2))
     (if-let [v (first coll)]
       (.copy v)
       (literal ""))
     (rest coll))))

(defn join-some [separator coll]
  (join separator (filterv some? coll)))

(defn- hover-event [hover-component]
  (net.minecraft.network.chat.HoverEvent/new
    net.minecraft.network.chat.HoverEvent$Action/SHOW_TEXT
    hover-component))

(defn hover [base-component hover-component]
  (.withStyle base-component
              #(.withHoverEvent % (hover-event hover-component))))

(defn red [component]
  (.withStyle component net.minecraft.ChatFormatting/RED))

(defn dark-gray [component]
  (.withStyle component net.minecraft.ChatFormatting/DARK_GRAY))

(defn blue [component]
  (.withStyle component net.minecraft.ChatFormatting/BLUE))

(defn italic [component]
  (.withStyle component net.minecraft.ChatFormatting/ITALIC))
