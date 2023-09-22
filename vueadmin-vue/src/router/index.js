import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Index from '../views/Index'
import User from '../views/sys/User'
import Menu from '../views/sys/Menu'
import Role from '../views/sys/Role'
import store from "../store";

import axios from "../axios";

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {
                path: '/index',
                name: 'Index',
                component: Index
            },
            {
                path: '/usercenter',
                name: 'UserCenter',
                meta: {
                  title: '个人中心'
                },
                component: () => import(/* webpackChunkName: "about" */ '../views/UserCenter.vue')
            },
            // {
            //     path: '/sys/users',
            //     name: 'SysUser',
            //     component: User
            // },
            // {
            //     path: '/sys/menus',
            //     name: 'SysMenu',
            //     component: Menu
            // },
            // {
            //     path: '/sys/roles',
            //     name: 'SysRole',
            //     component: Role
            // }
        ]
    },
    {
        path: '/login',
        name: 'Login',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "about" */ '../views/Login.vue')
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

router.beforeEach((to, from, next) => {

    let hasRoute = store.state.menus.hasRoute

    let token = localStorage.getItem('token');

    if (to.path == '/login') {
        next();
    } else if (!token) {
        next({path:'/login'});
    }

    if (!hasRoute && token) {

        axios.get('/sys/menu/nav', {
            headers: {
                Authorization: localStorage.getItem("token")
            }
        }).then(res => {

            //拿到menuList
            store.commit('setMenuList', res.data.data.nav);

            //拿到用户权限
            store.commit('setPermList', res.data.data.authorizaties);

            //动态绑定路由
            let newRoutes = router.options.routes;

            res.data.data.nav.forEach(menu => {

                if (menu.children) {

                    menu.children.forEach(e => {

                            //转成路由
                            let route = menuToRoute(e);

                            //把路由添加进管理中
                            if (route) {

                                newRoutes[0].children.push(route);

                            }

                        }
                    )

                }

            });

            console.log(newRoutes)
            //添加路由
            router.addRoutes(newRoutes);

            hasRoute = true;
            store.commit("changeRouteStatus",hasRoute)

        });
    }
    next();
});

//导航转成路由
const menuToRoute = (menu) => {

    if (!menu) {
        return null;
    }

    let route = {

        name: menu.name,
        path: menu.path,
        meta: {

            icon: menu.icon,
            title: menu.title

        }

    };

    route.component = () => import('@/views/' + menu.component + '.vue')
    console.log(route.component)
    return route;

}

export default router
