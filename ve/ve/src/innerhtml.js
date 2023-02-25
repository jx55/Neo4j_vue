let content=function(obj){
  let str="<ul>";
  if(obj.hasOwnProperty('name') && obj.name!="")
    str+="<li><span style='font-size:15px;color:#99CCFF;'>名字：</span>"+obj.name+"</li>";
  if(obj.hasOwnProperty('category') && obj.category!="")
    str+="<li><span style='font-size:15px;color:#99CCFF;'>所属：</span>"+obj.category+"</li>";
  if(obj.hasOwnProperty('age') && obj.age!="")
    str+="<li><span style='font-size:15px;color:#99CCFF;'>年龄：</span>"+obj.age+"岁</li>";
  if(obj.hasOwnProperty('weight') && obj.weight!="")
    str+="<li><span style='font-size:15px;color:#99CCFF;'>体重：</span>"+obj.weight+"kg</li>";
  if(obj.hasOwnProperty('labels') && obj.labels!="")
    str+="<li><span style='font-size:15px;color:#99CCFF;'>标签：</span>"+obj.labels+"</li>";

  if(obj.hasOwnProperty('compionNum') && obj.compionNum!="")
      str+="<li><span style='font-size:15px;color:#99CCFF;'>冠军数目：</span>"+obj.compionNum+"</li>";
  if(obj.hasOwnProperty('location') && obj.location!="")
      str+="<li><span style='font-size:15px;color:#99CCFF;'>位于：</span>"+obj.location+"</li>";

  if(obj.hasOwnProperty('description') && obj.description!="")
    str+="<li><p style='font-size:15px;color:#99CCFF;'>介绍：</p>"+obj.description+"</li>";
  str+="</ul>";
  return str;
}
export default content;
