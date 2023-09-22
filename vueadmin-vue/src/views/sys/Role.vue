<template>
    <div>

        <el-form :inline="true">
            <el-form-item>
                <el-input
                        v-model="searchForm.name"
                        placeholder="名称"
                        clearable
                ></el-input>
            </el-form-item>

            <el-form-item>
                <el-button @click="getRoleList">搜索</el-button>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="dialogVisible = true">新增</el-button>
            </el-form-item>

            <el-form-item>
                <el-popconfirm title="这是一段内容确定删除吗？" @confirm="delHandle(null)">
                    <el-button type="danger" slot="reference" :disabled="delBtlStatu">批量删除</el-button>
                </el-popconfirm>
            </el-form-item>
        </el-form>

        <template>
            <el-table
                    border
                    stripe
                    ref="multipleTable"
                    :data="tableData"
                    tooltip-effect="dark"
                    style="width: 100%"
                    @selection-change="handleSelectionChange">
                <el-table-column
                        type="selection"
                        width="55">
                </el-table-column>

                <el-table-column
                        prop="name"
                        label="名称"
                        width="120">
                </el-table-column>

                <el-table-column
                        prop="code"
                        label="唯一编码"
                        show-overflow-tooltip>
                </el-table-column>

                <el-table-column
                        prop="remark"
                        label="描述"
                        show-overflow-tooltip>
                </el-table-column>

                <el-table-column
                        prop="orderNum"
                        label="状态">
                    <template slot-scope="scope">
                        <el-tag size="small" v-if="scope.row.statu === 1" type="success">正常</el-tag>
                        <el-tag size="small" v-else-if="scope.row.statu === 0" type="danger">禁用</el-tag>
                    </template>
                </el-table-column>

                <el-table-column
                        prop="icon"
                        label="操作">

                    <template slot-scope="scope">
                        <el-button type="text" @click="editHandle(scope.row.id)">编辑</el-button>

                        <el-divider direction="vertical"></el-divider>

                        <el-button type="text" @click="permHandle(scope.row.id)">分配权限</el-button>

                        <el-divider direction="vertical"></el-divider>

                        <template>
                            <el-popconfirm title="这是一段内容确定删除吗？" @confirm="delHandle(scope.row.id)">
                                <el-button type="text" slot="reference">删除</el-button>
                            </el-popconfirm>
                        </template>

                    </template>

                </el-table-column>

            </el-table>

            <!-- 分页 -->
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    layout="total, sizes, prev, pager, next, jumper"
                    :page-sizes="[10, 20, 50, 100]"
                    :current-page="current"
                    :page-size="size"
                    :total="total">
            </el-pagination>

            <div style="margin-top: 20px">
            </div>
        </template>

        <!-- 对话框 -->
        <el-dialog
                title="新增"
                :visible.sync="dialogVisible"
                width="600px"
                :before-close="handleClose">
            <el-form :model="editForm" :rules="editFormRules" ref="editForm" label-width="100px" class="demo-editForm">

                <el-form-item label="角色名称" prop="name" label-width="100px">
                    <el-input v-model="editForm.name" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item label="唯一编码" prop="code" label-width="100px">
                    <el-input v-model="editForm.code" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item label="描述" prop="remark" label-width="100px">
                    <el-input v-model="editForm.remark" autocomplete="off"></el-input>
                </el-form-item>

                <el-form-item label="状态" prop="statu" label-width="100px">
                    <el-radio-group v-model="editForm.statu">
                        <el-radio :label=0>禁用</el-radio>
                        <el-radio :label=1>正常</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item>

                    <el-button type="primary" @click="submitForm('editForm')">提交</el-button>
                    <el-button @click="resetForm('editForm')">重置</el-button>

                </el-form-item>
            </el-form>
        </el-dialog>

        <!--分配权限弹窗-->
        <el-dialog
                title="权限分配"
                :visible.sync="permDialogVisible"
                width="600px">
            <el-form :model="editForm">
                <el-tree
                        :data="permTreeData"
                        show-checkbox
                        ref="permTree"
                        :default-expand-all="true"
                        node-key="id"
                        :check-strictly=true
                        :props="defaultProps">
                </el-tree>
            </el-form>

            <span slot="footer" class="dialog-footer">
                <el-button @click="permDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitFormHandle('permForm')">确 定</el-button>
            </span>

        </el-dialog>

    </div>


</template>

<script>
    export default {
        name: "Role",
        data() {
            return {

                /*搜索输入框*/
                searchForm: {},

                /*分页*/
                total: 0,
                size: 10,
                current: 1,

                /*新增弹窗*/
                dialogVisible: false,

                /*编辑的数据*/
                editForm: {},

                /*新增弹窗输入规则*/
                editFormRules: {
                    name: [
                        {required: true, message: '请输入角色名称', trigger: 'blur'}
                    ],
                    code: [
                        {required: true, message: '请输入唯一编码', trigger: 'blur'}
                    ],
                    statu: [
                        {required: true, message: '请选择状态', trigger: 'blur'}
                    ]
                },

                /*批量删除禁用与可用*/
                delBtlStatu: true,

                /*表单数据*/
                tableData: [],

                /*批量删除数组*/
                multipleSelection: [],

                /*权限分配弹窗*/
                permDialogVisible: false,

                /*权限分配数组*/
                permTreeData: [],

                /*权限分配节点*/
                defaultProps: {
                    children: 'children',
                    label: 'name'
                },
            }
        },
        created() {
            this.getRoleList();
        },
        methods: {
            toggleSelection(rows) {
                if (rows) {
                    rows.forEach(row => {
                        this.$refs.multipleTable.toggleRowSelection(row);
                    });
                } else {
                    this.$refs.multipleTable.clearSelection();
                }
            },
            /*批量删除,按钮判断*/
            handleSelectionChange(val) {
                console.log(val)
                this.multipleSelection = val;
                this.delBtlStatu = val.length == 0
            },
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
                this.size = val;
                this.getRoleList();
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                this.current = val;
                this.getRoleList();
            },
            resetForm(formName) {

                this.$refs[formName].resetFields();
                this.editForm = {}
                this.dialogVisible = false;

            },
            handleClose() {

                this.resetForm('editForm');

            },

            getRoleList() {
                this.$axios.get("/sys/role/list", {
                    params: {
                        name: this.searchForm.name,
                        size: this.size,
                        current: this.current
                    }

                }).then(res => {
                    this.tableData = res.data.data.records
                    this.total = res.data.data.total
                    this.size = res.data.data.size
                    this.current = res.data.data.current
                });
            },

            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.$axios.post('/sys/role/' + (this.editForm.id ? 'update' : 'save'), this.editForm).then(res => {
                            this.$message({
                                message: '操作成功',
                                type: 'success',
                            });

                            this.getRoleList();
                            this.dialogVisible = false;

                        });

                    }
                });
            },
            editHandle(id) {

                this.$axios.get('/sys/role/info/' + id).then(res => {
                    this.editForm = res.data.data
                    this.dialogVisible = true;
                });

            },
            delHandle(id) {

                var ids = [];

                if (id) {
                    ids.push(id);
                } else {
                    this.multipleSelection.forEach(row => {
                        ids.push(row.id);
                    });
                }

                this.$axios.post('/sys/role/delete', ids).then(res => {

                    this.$message({
                        message: '删除成功',
                        type: 'success',

                    });
                    this.getRoleList()

                });

            },
            /*权限分配弹窗*/
            permHandle(id) {

                /*查找权限*/
                this.$axios.get('/sys/menu/list').then(res => {
                    this.permTreeData = res.data.data;
                });

                /*回显赋值*/
                this.$axios.get('/sys/role/info/' + id).then(res => {

                    this.$refs.permTree.setCheckedKeys(res.data.data.menuIds)
                    this.permForm = res.data.data;

                });

                this.permDialogVisible = true;
            },
            /*权限菜单提交*/
            submitFormHandle(fomName) {

                var menuIds = this.$refs.permTree.getCheckedKeys();
                this.$axios.post('/sys/role/perm/' + this.permForm.id, menuIds).then(res => {
                    this.$message({
                        message: '操作成功',
                        type: 'success',
                        onClose: () => {
                            this.getRoleList()
                        }
                    });
                    this.permDialogVisible = false;
                });

            }
        }
    }
</script>

<style scoped>

    .el-pagination {
        float: right;
        margin-top: 22px;
    }

</style>