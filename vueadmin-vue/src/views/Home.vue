<template>
    <el-container>
        <el-aside width="200px">
            <SideMenu></SideMenu>
        </el-aside>
        <el-container>
            <el-header>

                <strong>VueAdmin后台管理系统</strong>

                <div class="header-avatar">

                    <el-avatar size="medium" :src="userinfo.avatar"></el-avatar>

                    <el-dropdown>
                      <span class="el-dropdown-link">
                        {{userinfo.username}}<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item >
                                <router-link to="/usercenter">个人中心</router-link>
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="logout">退出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>

                    <el-link href="">网站</el-link>
                    <el-link href="">更多</el-link>

                </div>

            </el-header>
            <el-main>
                <Tabs></Tabs>
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<script>

    import SideMenu from "./inc/SideMenu";
    import Tabs from "./inc/Tabs";

    export default {
        name: "Home",
        components: {SideMenu,Tabs},
        data() {
            return {
                userinfo: {
                    id: "",
                    username: "",
                    avatar: ""
                }
            }
        },
        created() {
            this.getUserInfo();
        },
        methods: {
            getUserInfo() {
                this.$axios.get("/sys/userinfo").then(res => {
                    this.userinfo = res.data.data;
                });
            },
            logout() {
                this.$axios.get('/logout').then(res => {
                    localStorage.clear();
                    sessionStorage.clear();

                    this.$store.commit("resetState");

                    this.$router.push('/login')
                });
            }
        }
    }
</script>

<style scoped>

    .el-icon-arrow-down {
        font-size: 12px;
    }

    .el-dropdown-link {
        cursor: pointer;
    }

    .header-avatar {
        float: right;
        width: 190px;
        display: flex;
        justify-content: space-around;
        align-items: center;
    }

    .el-container {
        height: 100%;
    }

    .el-header {
        background-color: #17B3A3;
        color: #333;
        text-align: center;
        line-height: 60px;
    }

    .el-aside {
        background-color: #D3DCE6;
        color: #333;
        line-height: 200px;
    }

    .el-main {
        background-color: #E9EEF3;
        /*text-align: center;*/
        /*line-height: 160px;*/
    }

    .el-menu-vertical-demo {
        height: 100%;
    }

</style>
