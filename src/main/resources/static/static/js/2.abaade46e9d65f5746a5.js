webpackJsonp([2],{IWYo:function(t,e){},ngcm:function(t,e){},sQ52:function(t,e,o){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=o("oqQY"),i=o.n(a),l=o("TEG0"),r={data:function(){return{dialogFormVisible:!1,form:{location:"",ip:"",type:"",createTime:""}}},methods:{clear:function(){this.form={location:"",ip:"",type:"",createTime:""},this.dialogFormVisible=!1},onSubmit:function(){var t=this,e=i()((new Date).getTime()-864e5).format("YYYY-MM-DD HH:mm:ss");this.$axios.post("/web/location/add",{location:this.form.location,ip:this.form.ip,type:this.form.type,createTime:e}).then(function(e){e&&200===e.data.code&&t.$emit("onSubmit"),t.clear(),t.$emit("refresh")})}}},n={render:function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",[o("el-button",{staticClass:"add-button-add",attrs:{size:"small",type:"primary"},on:{click:function(e){t.dialogFormVisible=!0}}},[t._v("添加")]),t._v(" "),o("el-dialog",{attrs:{title:"添加信息",visible:t.dialogFormVisible},on:{"update:visible":function(e){t.dialogFormVisible=e}}},[o("el-form",{attrs:{"v-model":t.form}},[o("el-form-item",{attrs:{label:"库位"}},[o("el-input",{model:{value:t.form.location,callback:function(e){t.$set(t.form,"location",e)},expression:"form.location"}})],1),t._v(" "),o("el-form-item",{attrs:{label:"绑定ip"}},[o("el-input",{model:{value:t.form.ip,callback:function(e){t.$set(t.form,"ip",e)},expression:"form.ip"}})],1),t._v(" "),o("el-form-item",{attrs:{label:"库位类型"}},[o("el-select",{staticStyle:{float:"left"},attrs:{placeholder:"请选择库位类型"},model:{value:t.form.type,callback:function(e){t.$set(t.form,"type",e)},expression:"form.type"}},[o("el-option",{attrs:{label:"放货库位",value:"deliveryLocation"}}),t._v(" "),o("el-option",{attrs:{label:"取货库位",value:"takeLocation"}})],1)],1)],1),t._v(" "),o("div",{attrs:{slot:"footer"},slot:"footer"},[o("el-button",{on:{click:t.clear}},[t._v("取 消")]),t._v(" "),o("el-button",{attrs:{type:"primary"},on:{click:t.onSubmit}},[t._v("添加")])],1)],1)],1)},staticRenderFns:[]};var s=o("VU/8")(r,n,!1,function(t){o("ngcm")},null,null).exports,c={name:"Content",components:{SearchBar:l.a,ConAddForm:s},data:function(){return{dialogEditFormVisible:!1,pagesize:10,currentPage:1,locations:[],sizes:0,isSear:0,form:{id:"",location:"",ip:"",lock:"",taskId:"",type:"",createTime:""}}},mounted:function(){this.loadLocations()},methods:{loadLocations:function(){var t=this;this.locations=[];for(var e=0;e<this.currentPage;e++)this.$axios.get("/web/location/list?page="+e+"&size="+this.pagesize,{}).then(function(e){t.locations=t.locations.concat(e.data.data.content),t.sizes=e.data.data.totalElements})},show:function(t){var e=!0;"未占用"==t.lock&&(e=!1),this.dialogEditFormVisible=!0,this.form={id:t.id,location:t.location,ip:t.ip,lock:e,taskId:t.taskId,type:t.type,createTime:t.createTime}},clear:function(){this.form={id:"",location:"",ip:"",lock:"",taskId:"",type:""},this.dialogEditFormVisible=!1},editLocations:function(){var t=this,e=i()((new Date).getTime()-864e5).format("YYYY-MM-DD HH:mm:ss");this.$axios.post("/web/location/edit",{id:this.form.id,location:this.form.location,ip:this.form.ip,lock:this.form.lock,taskId:this.form.taskId,type:this.form.type,createTime:this.form.createTime,updateTime:e}).then(function(e){e&&200===e.data.code&&t.$emit("editLocations"),t.loadLocations(),t.clear()})},deleteLocations:function(t){var e=this;this.$confirm("确认删除？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){e.$axios.delete("/web/location/delete?id="+t.id,{}).then(function(o){e.loadLocations(),e.$emit("deleteLocations",t)})}).catch(function(){e.$message({type:"info",message:"已取消"})})},searchResult:function(){var t=this;""!=this.$refs.searchBar.keywords?(this.$axios.get("/web/location/find?location="+this.$refs.searchBar.keywords,{}).then(function(e){t.locations=e.data.data,t.sizes=t.locations.length}),this.isSear=1):(this.isSear=0,this.loadLocations())},handleSizeChange:function(t){this.pagesize=t,0===this.isSear&&this.loadLocations()},handleCurrentChange:function(t){this.currentPage=t,0===this.isSear&&this.loadLocations()},forLockmatter:function(t,e){return 1==t.lock&&(t.lock="已占用"),0==t.lock&&(t.lock="未占用"),t.lock},forTypematter:function(t,e){return"takeLocation"==t.type&&(t.type="取货库位"),"deliveryLocation"==t.type&&(t.type="放货库位"),t.type}}},m={render:function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",[o("div",{staticClass:"con-div-one"},[o("el-breadcrumb",{attrs:{separator:"/"}},[o("el-breadcrumb-item",[t._v("首页")]),t._v(" "),o("el-breadcrumb-item",[t._v("库位管理")])],1)],1),t._v(" "),o("div",{staticClass:"con-div-two"},[o("el-row",[o("search-bar",{ref:"searchBar",staticClass:"con-search-bar",on:{onSearch:t.searchResult}}),t._v(" "),o("con-add-form",{staticClass:"con-add",on:{refresh:t.loadLocations}})],1),t._v(" "),o("div",[o("el-table",{staticStyle:{width:"100%"},attrs:{data:t.locations.slice((t.currentPage-1)*t.pagesize,t.currentPage*t.pagesize),border:"",height:"735px"}},[o("el-table-column",{attrs:{prop:"id",label:"ID",width:"150"}}),t._v(" "),o("el-table-column",{attrs:{prop:"location",label:"库位",width:"200"}}),t._v(" "),o("el-table-column",{attrs:{prop:"ip",label:"绑定ip",width:"200"}}),t._v(" "),o("el-table-column",{attrs:{prop:"lock",label:"占用情况",width:"200",formatter:t.forLockmatter}}),t._v(" "),o("el-table-column",{attrs:{prop:"taskId",label:"占用taskId",width:"200"}}),t._v(" "),o("el-table-column",{attrs:{prop:"type",label:"库位类型",width:"200",formatter:t.forTypematter}}),t._v(" "),o("el-table-column",{attrs:{prop:"createTime",label:"创建时间",width:"200"}}),t._v(" "),o("el-table-column",{attrs:{prop:"updateTime",label:"更新时间",width:"200"}}),t._v(" "),o("el-table-column",{attrs:{fixed:"right",label:"操作",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-button",{attrs:{type:"text",size:"small"},on:{click:function(o){return t.show(e.row)}}},[t._v("修改")]),t._v(" "),o("el-button",{attrs:{type:"text",size:"small"},on:{click:function(o){return t.deleteLocations(e.row)}}},[t._v("删除")])]}}])})],1),t._v(" "),o("div",[o("el-pagination",{attrs:{"current-page":t.currentPage,"page-sizes":[10,15,20],"page-size":10,layout:"total, sizes, prev, pager, next, jumper",total:t.sizes},on:{"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}})],1)],1)],1),t._v(" "),o("div",[o("el-dialog",{attrs:{title:"修改信息",visible:t.dialogEditFormVisible},on:{"update:visible":function(e){t.dialogEditFormVisible=e}}},[o("el-form",{attrs:{"v-model":t.form}},[o("el-form-item",{attrs:{label:"库位"}},[o("el-input",{model:{value:t.form.location,callback:function(e){t.$set(t.form,"location",e)},expression:"form.location"}})],1),t._v(" "),o("el-form-item",{attrs:{label:"绑定ip"}},[o("el-input",{model:{value:t.form.ip,callback:function(e){t.$set(t.form,"ip",e)},expression:"form.ip"}})],1),t._v(" "),o("el-form-item",{attrs:{label:"是否占用"}},[o("el-checkbox",{staticStyle:{float:"left"},model:{value:t.form.lock,callback:function(e){t.$set(t.form,"lock",e)},expression:"form.lock"}})],1),t._v(" "),o("el-form-item",{attrs:{label:"占用taskId"}},[o("el-input",{model:{value:t.form.taskId,callback:function(e){t.$set(t.form,"taskId",e)},expression:"form.taskId"}})],1),t._v(" "),o("el-form-item",{attrs:{label:"库位类型"}},[o("el-select",{staticStyle:{float:"left"},attrs:{placeholder:"请选择库位类型"},model:{value:t.form.type,callback:function(e){t.$set(t.form,"type",e)},expression:"form.type"}},[o("el-option",{attrs:{label:"放货库位",value:"deliveryLocation"}}),t._v(" "),o("el-option",{attrs:{label:"取货库位",value:"takeLocation"}})],1)],1)],1),t._v(" "),o("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[o("el-button",{on:{click:t.clear}},[t._v("取 消")]),t._v(" "),o("el-button",{attrs:{type:"primary"},on:{click:t.editLocations}},[t._v("修改")])],1)],1)],1)])},staticRenderFns:[]};var d=o("VU/8")(c,m,!1,function(t){o("IWYo")},null,null);e.default=d.exports}});
//# sourceMappingURL=2.abaade46e9d65f5746a5.js.map