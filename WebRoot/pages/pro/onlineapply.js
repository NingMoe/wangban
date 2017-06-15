function setBusinessAttach(obj,uid){
	var cmd = new LEx.Command("app.icity.project.WdwpCmd");
	cmd.setParameter("obj", obj);
	cmd.setParameter("TYPE", "0");
	cmd.setParameter("STATE", "0");
	cmd.setParameter("UID", uid+"");
	var ret =  cmd.execute("setBusinessAttach");
	if(!ret.error){
		return true;
	}else{
		return false;
	}
}