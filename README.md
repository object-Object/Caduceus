# Caduceus

[![powered by hexdoc](https://img.shields.io/endpoint?url=https://hexxy.media/api/v0/badge/hexdoc?label=1)](https://github.com/hexdoc-dev/hexdoc)

A Clojure-based Hex Casting addon for advanced meta-evaluation. Created for HexJam 2025.

## Developing

IMPORTANT: Type hints should be **avoided** whenever possible. Clojure seems to compile them into code like `RT.classForName("net.minecraft.resources.ResourceLocation`, which fails on Fabric because it isn't remapped.

I think this should be fixable using [elide-meta](https://clojure.org/reference/compilation#_compiler_options), but Clojurephant's elideMeta option doesn't seem to be working.

Additionally, avoid using `:import` from remapped packages (ie. `net.minecraft`), since Clojure's implementation of `:import` also results in loading classes by name at runtime.
