<template>
    <el-tabs v-model="editableTabsValue" type="card" closable @tab-remove="removeTab" @tab-click="clickTable">
        <el-tab-pane
                v-for="(item, index) in editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
        >
            {{item.content}}
        </el-tab-pane>
    </el-tabs>
</template>

<script>
    import VueRouter from "vue-router";

    export default {
        name: "Tabs",
        data() {
            return {
                // editableTabsValue: this.$store.state.menus.editableTabsValue,
                // editableTabs: [this.$store.state.menus.editableTabs]
            }
        },
        computed: {
            editableTabs: {
                get() {
                    return this.$store.state.menus.editableTabs
                },
                set(val) {
                    this.$store.state.menus.editableTabs = val
                }
            },
            editableTabsValue: {
                get() {
                    return this.$store.state.menus.editableTabsValue
                },
                set(val) {
                    this.$store.state.menus.editableTabsValue = val
                }
            }
        },
        methods: {
            removeTab(targetName) {
                let tabs = this.editableTabs;
                let activeName = this.editableTabsValue;

                if (targetName == 'Index') {
                    return
                }

                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }

                this.editableTabsValue = activeName;
                this.editableTabs = tabs.filter(tab => tab.name !== targetName);

                this.$router.push({name: activeName})
            },
            clickTable(target) {
                // 获取原型对象中的push方法
                const originalPush = VueRouter.prototype.push;
                // 修改原型对象中的push方法
                VueRouter.prototype.push = function push(location) {
                    return originalPush.call(this, location).catch(err => err)
                }
                this.$router.push({name: target.name})
            }
        }
    }
</script>

<style scoped>

</style>