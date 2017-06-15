/**
 * 基于Jpower的日期时间框
 *
 * @param dateTime
 */
//function CFormDateTime(dateTime) {
//    var format = getJavaDateFormat($(dateTime).attr("format"));
//    Power.form.dateTimePicker(dateTime.id,{
//        format:format
//    });
//}
//
///**
// * 基于Jpower的日期框
// *
// * @param date
// */
//function CFormDate(date) {
//    var format = getJavaDateFormat($(date).attr("format"));
//    Power.form.dateTimePicker(date.id,{
//        format:format,
//        minView:'month'
//    });
//}

//'yyyy-MM-dd HH:mm:ss

function getJavaDateFormat(f){
    f = f.replace(/Y/, 'yyyy')
        .replace(/m/, 'MM')
        .replace(/d/, 'dd')
        .replace(/H/, 'HH')
        .replace(/i/, 'mm')
        .replace(/s/, 'ss');
    return f;
}


function CFormDateTime(dateTime) {
    var format = getJavaDateFormat($(dateTime).attr("format"));
    $(dateTime).on("click",function(){
        WdatePicker({dateFmt:format});
    });
}

function CFormDate(date) {
    var format = getJavaDateFormat($(date).attr("format"));
    $(date).on("click",function(){
        WdatePicker({dateFmt:format});
    });
}