from importlib.resources import Package

from hexdoc.plugin import (
    HookReturn,
    ModPlugin,
    ModPluginImpl,
    ModPluginWithBook,
    hookimpl,
)
from typing_extensions import override

import hexdoc_caduceus

from .__gradle_version__ import FULL_VERSION, MINECRAFT_VERSION, MOD_ID, MOD_VERSION
from .__version__ import PY_VERSION


class CaduceusPlugin(ModPluginImpl):
    @staticmethod
    @hookimpl
    def hexdoc_mod_plugin(branch: str) -> ModPlugin:
        return CaduceusModPlugin(branch=branch)


class CaduceusModPlugin(ModPluginWithBook):
    @property
    @override
    def modid(self) -> str:
        return MOD_ID

    @property
    @override
    def full_version(self) -> str:
        return FULL_VERSION

    @property
    @override
    def mod_version(self) -> str:
        return f"{MOD_VERSION}+{MINECRAFT_VERSION}"

    @property
    @override
    def plugin_version(self) -> str:
        return PY_VERSION

    @override
    def resource_dirs(self) -> HookReturn[Package]:
        # lazy import because generated may not exist when this file is loaded
        # eg. when generating the contents of generated
        # so we only want to import it if we actually need it
        from ._export import generated

        return generated

    @override
    def jinja_template_root(self) -> tuple[Package, str]:
        return hexdoc_caduceus, "_templates"
