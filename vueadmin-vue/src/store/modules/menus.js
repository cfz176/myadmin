import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default {
    state: {

        menuList: [],
        permList: [],

        hasRoute: false,

        editableTabsValue: 'Index',
        editableTabs: [{
            title: '首页',
            name: 'Index',
        }],


    },
    mutations: {

        setMenuList(state, menus) {
            state.menuList = menus
        },
        setPermList(state, perm) {
            state.permList = perm
        },

        changeRouteStatus(state, hasRoute) {
            state.hasRoute = hasRoute;
        },

        addTab(state, tab) {
            let index = state.editableTabs.findIndex(e =>e.name === tab.name)

            if (index == -1) {
                state.editableTabs.push({
                    title: tab.title,
                    name: tab.name
                });
            }
            state.editableTabsValue = tab.name;
        },

        resetState: (state) => {
            state.menuList = [];
            state.permList = [];
            state.hasRoute = false;
            state.editableTabsValue = 'Index';
            state.editableTabs =  [{
                title: '首页',
                name: 'Index',
            }];
            state.token = '';
        }

    },
    actions: {},
}
