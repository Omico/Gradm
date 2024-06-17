import type {SidebarOptions} from "@vuepress/theme-default"

export const sidebar_en_us: SidebarOptions = {
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
