import type {SidebarConfig} from "@vuepress/theme-default"

export const sidebar_en_us: SidebarConfig = {
    "/": [
        {
            text: "Guide",
            children: [
                "/README.md",
                "/getting-started.md",
                "/composite-build.md",
            ],
        },
    ],
}
