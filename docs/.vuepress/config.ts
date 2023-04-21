import {backToTopPlugin} from "@vuepress/plugin-back-to-top"
import {gitPlugin} from "@vuepress/plugin-git"
import {defaultTheme, defineUserConfig} from "vuepress"
import {sidebar_en_us, sidebar_zh_hans} from "./configs"

export default defineUserConfig(
    {
        locales: {
            "/": {
                lang: "en-US",
                title: "Gradm",
            },
            "/zh-Hans/": {
                lang: "zh-Hans",
                title: "Gradm",
            },
        },
        theme: defaultTheme(
            {
                docsRepo: "Omico/Gradm",
                docsDir: "docs",
                docsBranch: "main",
                editLink: true,
                locales: {
                    "/": {
                        sidebar: sidebar_en_us,
                    },
                    "/zh-Hans/": {
                        sidebar: sidebar_zh_hans,
                        selectLanguageName: "简体中文",
                        selectLanguageText: "语言",
                        selectLanguageAriaLabel: "选择语言",
                        editLinkText: "在 GitHub 上编辑此页",
                        lastUpdatedText: "上次更新",
                        contributorsText: "贡献者",
                    },
                },
            },
        ),
        plugins: [
            backToTopPlugin(),
            gitPlugin(
                {
                    contributors: false,
                },
            ),
        ],
    },
)
