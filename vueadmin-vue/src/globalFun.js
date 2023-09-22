import Vue from 'vue'

Vue.mixin({
    methods: {
        hasAuth(perm) {
            var authorization = this.$store.state.menus.permList

            return authorization.indexOf(perm) > -1;
        }
    }
});