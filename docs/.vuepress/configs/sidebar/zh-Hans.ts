import type {SidebarOptions} from "@vuepress/theme-default"

export const sidebar_zh_hans: SidebarOptions = {
    "/zh-Hans/": [
        {
            text: "指南",
            children: [
                "/zh-Hans/README.md",
                "/zh-Hans/getting-started.md",
                "/zh-Hans/composite-build.md",
            ],
        },
    ],
}
