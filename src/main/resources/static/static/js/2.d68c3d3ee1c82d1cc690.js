webpackJsonp([2],{sQ52:function(t,e,i){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o=i("he3m"),a=i.n(o),l=i("TEG0"),n={data:function(){return{dialogFormVisible:!1,isDisabled:!0,dtauxiliary:"00",locations:[],form:{location:"",ip:"",type:"",auxiliarylocation:"",teethH:.5,createTime:""}}},methods:{clear:function(){this.form={location:"",ip:"",type:"",auxiliarylocation:"",teethH:.5,createTime:""},this.dialogFormVisible=!1},onSubmit:function(){var t=this,e=a()((new Date).getTime()-864e5).format("YYYY-MM-DD HH:mm:ss");this.$axios.post("/web/location/add",{location:this.form.location,ip:this.form.ip,type:this.form.type,auxiliarylocation:this.form.auxiliarylocation,teethH:this.form.teethH,createTime:e}).then(function(e){e&&200===e.data.code&&t.$emit("onSubmit"),t.clear(),t.$emit("refresh")})},addLoadLocations:function(){var t=this;this.locations=[],this.$axios.get("/web/location/findbytype?type=auxiliary",{}).then(function(e){t.locations=e.data.data})},addifLocation:function(){"deliveryLocation"===this.form.type?(this.isDisabled=!1,this.dtauxiliary="10",this.addLoadLocations()):"takeLocation"===this.form.type?(this.isDisabled=!1,this.dtauxiliary="01",this.addLoadLocations()):this.isDisabled=!0}}},r={render:function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("el-button",{staticClass:"add-button-add",attrs:{size:"small",type:"primary"},on:{click:function(e){t.dialogFormVisible=!0}}},[t._v("添加")]),t._v(" "),i("el-dialog",{attrs:{title:"添加信息",visible:t.dialogFormVisible},on:{"update:visible":function(e){t.dialogFormVisible=e}}},[i("el-form",{attrs:{"v-model":t.form}},[i("el-form-item",{attrs:{label:"库位"}},[i("el-input",{staticClass:"addContent",model:{value:t.form.location,callback:function(e){t.$set(t.form,"location",e)},expression:"form.location"}})],1),t._v(" "),i("el-form-item",{attrs:{label:"绑定ip"}},[i("el-input",{staticClass:"addContent",model:{value:t.form.ip,callback:function(e){t.$set(t.form,"ip",e)},expression:"form.ip"}})],1),t._v(" "),i("el-form-item",{attrs:{label:"库位类型"}},[i("el-select",{staticClass:"addContent",attrs:{placeholder:"请选择库位类型"},on:{change:function(e){return t.addifLocation()}},model:{value:t.form.type,callback:function(e){t.$set(t.form,"type",e)},expression:"form.type"}},[i("el-option",{attrs:{label:"放货库位",value:"deliveryLocation"}}),t._v(" "),i("el-option",{attrs:{label:"取货库位",value:"takeLocation"}}),t._v(" "),i("el-option",{attrs:{label:"辅助点",value:"auxiliary"}})],1)],1),t._v(" "),i("el-form-item",{attrs:{label:"绑定辅助点"}},[i("el-select",{staticClass:"addContent",attrs:{placeholder:"请选择辅助点"},model:{value:t.form.auxiliarylocation,callback:function(e){t.$set(t.form,"auxiliarylocation",e)},expression:"form.auxiliarylocation"}},t._l(t.locations,function(t){return i("el-option",{key:t.location,attrs:{label:t.location,value:t.location}})}),1)],1),t._v(" "),i("el-form-item",{attrs:{label:"插齿高度"}},[i("el-input",{staticClass:"addContent",model:{value:t.form.teethH,callback:function(e){t.$set(t.form,"teethH",e)},expression:"form.teethH"}})],1)],1),t._v(" "),i("div",{attrs:{slot:"footer"},slot:"footer"},[i("el-button",{on:{click:t.clear}},[t._v("取 消")]),t._v(" "),i("el-button",{attrs:{type:"primary"},on:{click:t.onSubmit}},[t._v("添加")])],1)],1)],1)},staticRenderFns:[]};var s=i("C7Lr")(n,r,!1,function(t){i("xxw2")},null,null).exports,c={name:"Content",components:{SearchBar:l.a,ConAddForm:s},data:function(){return{dialogEditFormVisible:!1,pagesize:10,currentPage:1,locations:[],auxiliarylocations:[],sizes:0,isSear:0,form:{id:"",location:"",ip:"",lock:"",type:"",auxiliarylocation:"",teethH:0,createTime:""}}},mounted:function(){this.loadLocations()},methods:{loadLocations:function(){var t=this;this.locations=[];for(var e=0;e<this.currentPage;e++)this.$axios.get("/web/location/list?page="+e+"&size="+this.pagesize,{}).then(function(e){t.locations=t.locations.concat(e.data.data.content),t.sizes=e.data.data.totalElements})},loadAuxiliaryLocations:function(){var t=this;this.auxiliarylocations=[],this.$axios.get("/web/location/findbytype?type=auxiliary",{}).then(function(e){t.auxiliarylocations=e.data.data})},show:function(t){var e=!0;"未占用"==t.lock&&(e=!1),this.dialogEditFormVisible=!0,this.form={id:t.id,location:t.location,ip:t.ip,lock:e,type:t.type,auxiliarylocation:t.auxiliarylocation,teethH:t.teethH,createTime:t.createTime},this.loadAuxiliaryLocations()},clear:function(){this.form={id:"",location:"",ip:"",lock:"",type:"",auxiliarylocation:"",teethH:0},this.dialogEditFormVisible=!1},editLocations:function(){var t=this,e=a()((new Date).getTime()-864e5).format("YYYY-MM-DD HH:mm:ss");"放货库位"===this.form.type&&(this.form.type="deliveryLocation"),"取货库位"===this.form.type&&(this.form.type="takeLocation"),"辅助点"===this.form.type&&(this.form.type="auxiliary"),this.$axios.post("/web/location/edit",{id:this.form.id,location:this.form.location,ip:this.form.ip,lock:this.form.lock,type:this.form.type,auxiliarylocation:this.form.auxiliarylocation,teethH:this.form.teethH,createTime:this.form.createTime,updateTime:e}).then(function(e){e&&200===e.data.code&&t.$emit("editLocations"),t.loadLocations(),t.clear()})},deleteLocations:function(t){var e=this;this.$confirm("确认删除？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){e.$axios.delete("/web/location/delete?id="+t.id,{}).then(function(i){e.loadLocations(),e.$emit("deleteLocations",t)})}).catch(function(){e.$message({type:"info",message:"已取消"})})},searchResult:function(){var t=this;""!=this.$refs.searchBar.keywords?(this.$axios.get("/web/location/find?location="+this.$refs.searchBar.keywords,{}).then(function(e){t.locations=e.data.data,t.sizes=t.locations.length}),this.isSear=1):(this.isSear=0,this.loadLocations())},handleSizeChange:function(t){this.pagesize=t,0===this.isSear&&this.loadLocations()},handleCurrentChange:function(t){this.currentPage=t,0===this.isSear&&this.loadLocations()},forLockmatter:function(t,e){return 1==t.lock&&(t.lock="已占用"),0==t.lock&&(t.lock="未占用"),t.lock},forTypematter:function(t,e){return"takeLocation"==t.type&&(t.type="取货库位"),"deliveryLocation"==t.type&&(t.type="放货库位"),"auxiliary"==t.type&&(t.type="辅助点"),t.type},ifLocation:function(){"deliveryLocation"===this.form.type?(this.isDisabled=!1,this.dtauxiliary="10",this.loadAuxiliaryLocations()):"takeLocation"===this.form.type?(this.isDisabled=!1,this.dtauxiliary="01",this.loadAuxiliaryLocations()):this.isDisabled=!0}}},d={render:function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("div",{staticClass:"con-div-one"},[i("el-breadcrumb",{attrs:{separator:"/"}},[i("el-breadcrumb-item",[t._v("首页")]),t._v(" "),i("el-breadcrumb-item",[t._v("库位管理")])],1)],1),t._v(" "),i("div",{staticClass:"con-div-two"},[i("el-row",[i("search-bar",{ref:"searchBar",staticClass:"con-search-bar",on:{onSearch:t.searchResult}}),t._v(" "),i("con-add-form",{staticClass:"con-add",on:{refresh:t.loadLocations}})],1),t._v(" "),i("div",[i("el-table",{staticStyle:{width:"100%"},attrs:{data:t.locations.slice((t.currentPage-1)*t.pagesize,t.currentPage*t.pagesize),border:"",height:"735px"}},[i("el-table-column",{attrs:{prop:"id",label:"ID",width:"150"}}),t._v(" "),i("el-table-column",{attrs:{prop:"location",label:"库位",width:"200"}}),t._v(" "),i("el-table-column",{attrs:{prop:"ip",label:"绑定ip",width:"200"}}),t._v(" "),i("el-table-column",{attrs:{prop:"lock",label:"占用情况",width:"200",formatter:t.forLockmatter}}),t._v(" "),i("el-table-column",{attrs:{prop:"taskId",label:"占用taskId",width:"200"}}),t._v(" "),i("el-table-column",{attrs:{prop:"type",label:"库位类型",width:"200",formatter:t.forTypematter}}),t._v(" "),i("el-table-column",{attrs:{prop:"auxiliarylocation",label:"绑定辅助点",width:"200"}}),t._v(" "),i("el-table-column",{attrs:{prop:"teethH",label:"插齿高度",width:"100"}}),t._v(" "),i("el-table-column",{attrs:{prop:"createTime",label:"创建时间",width:"200"}}),t._v(" "),i("el-table-column",{attrs:{prop:"updateTime",label:"更新时间",width:"200"}}),t._v(" "),i("el-table-column",{attrs:{fixed:"right",label:"操作",width:"120"},scopedSlots:t._u([{key:"default",fn:function(e){return[i("el-button",{attrs:{type:"text",size:"small"},on:{click:function(i){return t.show(e.row)}}},[t._v("修改")]),t._v(" "),i("el-button",{attrs:{type:"text",size:"small"},on:{click:function(i){return t.deleteLocations(e.row)}}},[t._v("删除")])]}}])})],1),t._v(" "),i("div",[i("el-pagination",{attrs:{"current-page":t.currentPage,"page-sizes":[10,15,20],"page-size":10,layout:"total, sizes, prev, pager, next, jumper",total:t.sizes},on:{"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}})],1)],1)],1),t._v(" "),i("div",[i("el-dialog",{attrs:{title:"修改信息",visible:t.dialogEditFormVisible},on:{"update:visible":function(e){t.dialogEditFormVisible=e}}},[i("el-form",{attrs:{"v-model":t.form}},[i("el-form-item",{attrs:{label:"库位"}},[i("el-input",{model:{value:t.form.location,callback:function(e){t.$set(t.form,"location",e)},expression:"form.location"}})],1),t._v(" "),i("el-form-item",{attrs:{label:"绑定ip"}},[i("el-input",{model:{value:t.form.ip,callback:function(e){t.$set(t.form,"ip",e)},expression:"form.ip"}})],1),t._v(" "),i("el-form-item",{attrs:{label:"是否占用"}},[i("el-checkbox",{staticStyle:{float:"left"},model:{value:t.form.lock,callback:function(e){t.$set(t.form,"lock",e)},expression:"form.lock"}})],1),t._v(" "),i("el-form-item",{attrs:{label:"库位类型"}},[i("el-select",{staticStyle:{float:"left"},attrs:{placeholder:"请选择库位类型"},on:{change:function(e){return t.ifLocation()}},model:{value:t.form.type,callback:function(e){t.$set(t.form,"type",e)},expression:"form.type"}},[i("el-option",{attrs:{label:"放货库位",value:"deliveryLocation"}}),t._v(" "),i("el-option",{attrs:{label:"取货库位",value:"takeLocation"}}),t._v(" "),i("el-option",{attrs:{label:"辅助点",value:"auxiliary"}})],1)],1),t._v(" "),i("el-form-item",{attrs:{label:"绑定辅助点"}},[i("el-select",{staticClass:"addContent",attrs:{placeholder:"请选择辅助点"},model:{value:t.form.auxiliarylocation,callback:function(e){t.$set(t.form,"auxiliarylocation",e)},expression:"form.auxiliarylocation"}},t._l(t.auxiliarylocations,function(t){return i("el-option",{key:t.location,attrs:{label:t.location,value:t.location}})}),1)],1),t._v(" "),i("el-form-item",{attrs:{label:"插齿高度"}},[i("el-input",{model:{value:t.form.teethH,callback:function(e){t.$set(t.form,"teethH",e)},expression:"form.teethH"}})],1)],1),t._v(" "),i("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[i("el-button",{on:{click:t.clear}},[t._v("取 消")]),t._v(" "),i("el-button",{attrs:{type:"primary"},on:{click:t.editLocations}},[t._v("修改")])],1)],1)],1)])},staticRenderFns:[]};var u=i("C7Lr")(c,d,!1,function(t){i("usgh")},null,null);e.default=u.exports},usgh:function(t,e){},xxw2:function(t,e){}});
//# sourceMappingURL=2.d68c3d3ee1c82d1cc690.js.map