/*
Scope : Jenkins CI CD Pipeline Template for  all Project
Parameters :
  propertyFileLoc - Project pipeline Property file location  
*/
def call() 
{
 def propertyFileLoc = "D:\\Jenkins\\Configuration.xml"
 
	stage('BuildAvailability') 
	{
		def methodcall = new BuildLibrary.BuildAvailability()
		methodcall.BuildAvailCall(propertyFileLoc)
		echo "BuildAvailability completed"
    }
    stage('VmSetup') 
	{
		def methodcall = new VmSetup.VmOperation()
		methodcall.VMOperationCall(propertyFileLoc)
		echo "Vm operations completed"
    }
    stage('ServerSetup')
	{
	 	def methodcall = new BuildLibrary.BuildOperation()
		methodcall.BuildOperationCall(propertyFileLoc)
		echo "ServerSetup Stage Completed" 
    }
	stage('ClientSetup')
	{
		echo "ClientSetup stage"
    }
	stage('SonarQube')
	{
		echo "SonarQube stage"
    }
}
