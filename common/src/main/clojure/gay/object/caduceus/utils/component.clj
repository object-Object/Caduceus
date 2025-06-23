(ns gay.object.caduceus.utils.component)

(defn literal [text]
  (net.minecraft.network.chat.Component/literal text))

(defn translatable [key & args]
  (net.minecraft.network.chat.Component/translatable key (object-array args)))

(defn translatable-with-fallback [key fallback]
  (net.minecraft.network.chat.Component/translatableWithFallback key fallback))

(defn join
  ([coll] (join "" coll))
  ([^String separator coll]
   (reduce
     #(-> ^net.minecraft.network.chat.MutableComponent %1
          (.append separator)
          (.append ^net.minecraft.network.chat.MutableComponent %2))
     (if-let [v (first coll)]
       (.copy ^net.minecraft.network.chat.Component v)
       (literal ""))
     (rest coll))))

(defn join-some [separator coll]
  (join separator (filterv some? coll)))

(defn- hover-event [hover-component]
  (net.minecraft.network.chat.HoverEvent/new
    net.minecraft.network.chat.HoverEvent$Action/SHOW_TEXT
    hover-component))

(defn hover [^net.minecraft.network.chat.MutableComponent base-component
             ^net.minecraft.network.chat.Component hover-component]
  (.withStyle base-component
              #(.withHoverEvent ^net.minecraft.network.chat.Style % (hover-event hover-component))))

(defn red [^net.minecraft.network.chat.MutableComponent component]
  (.withStyle component net.minecraft.ChatFormatting/RED))

(defn dark-gray [^net.minecraft.network.chat.MutableComponent component]
  (.withStyle component net.minecraft.ChatFormatting/DARK_GRAY))

(defn blue [^net.minecraft.network.chat.MutableComponent component]
  (.withStyle component net.minecraft.ChatFormatting/BLUE))

(defn italic [^net.minecraft.network.chat.MutableComponent component]
  (.withStyle component net.minecraft.ChatFormatting/ITALIC))
