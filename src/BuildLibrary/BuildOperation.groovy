package BuildLibrary
import groovy.json.JsonSlurperClassic
import groovy.json.JsonSlurper
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.NodeChild
import groovy.util.XmlParser
import groovy.util.slurpersupport.GPathResult

def BuildOperationCall(def FileLocation)
{
//InputFile Keywords and given variables should be matched
	def BuildDownload = "Download"
	def BuildCopy = "Copy"
	def TriggerExecution ="ExecutionFile"
	def parallelExec = [:]         //Empty map for parallel execution
	def hostnames = []	      
    def readFileContents = new File(FileLocation).getText()
    def config = new XmlSlurper().parseText(readFileContents)
	config.'ServerSetup'.'Machine'.each {node -> hostnames.push(node.@'host')}
    hostnames = hostnames.toArray();
	println "Hostname "+hostnames.toString()
    for (host in hostnames) {
		def action = [];
		def BuildDesc = [];
		def BuildOutloc = [];
        def nodeName = host.toString();
        parallelExec [nodeName] = {
		node(nodeName) {
			config.'ServerSetup'.'**'.find { node ->
			if(node.@'host' == nodeName){node.'Operation'.each {node1 -> action.push(node1.@'action')}
			}}
			config.'ServerSetup'.'**'.find { node -> 
			if(node.@'host' == nodeName){node.'Operation'.each {node1 -> BuildDesc.push(node1.@'BuildDesc')}
			}} 
			config.'ServerSetup'.'**'.find { node ->
			if(node.@'host' == nodeName){node.'Operation'.each {node1 -> BuildOutloc.push(node1.@'BuildOutloc')}
			}}
			echo "Hostname: ${nodeName}; Actions: ${action}; BuildDesc: ${BuildDesc}; BuildOutloc: ${BuildOutloc}"
			for(int i=0;i<action.size; i++){
				def actions =action[i].toString()
				def builddesc = BuildDesc[i].toString()
				def buildoutloc = BuildOutloc[i].toString()
				if(actions.contains(BuildDownload)){
					def BuildUrl= config.BuildAvailability.'**'.find { Server -> Server['@Desc'] == builddesc}['@URL'].toString()
					def JobName = config.BuildAvailability.'**'.find { Server -> Server['@Desc'] == builddesc}['@Job'].toString()
					def BuildChoice = config.BuildAvailability.'**'.find { Server -> Server['@Desc'] == builddesc}['@BuildChoice'].toString()
				if (BuildChoice == "LastSuccessful") {
                      BuildChoice = "lastSuccessfulBuild"
                    }
                else if (BuildChoice == "Predefined") {
                      BuildChoice = config.BuildAvailability.'**'.find { Server -> Server['@Desc'] == builddesc}['@PredefinedBuildID'].toString()
                    }
				else if (BuildChoice == "Today") {
                      BuildChoice = "lastBuild"
                    }
				def downloadUrl = "${BuildUrl}/job/${JobName}/${BuildChoice}/artifact/*zip*/archive.zip"
				println "downloadUrl:"+downloadUrl
				def filePath=buildoutloc+"\\"+"archive.zip"
				
				//Download latest build 
				DownloadByHttpReq(filePath,downloadUrl)
				 
				//Extract the Build
				fileUnZipOperation(filePath,buildoutloc)
				 
			}
			else if(actions.contains(BuildCopy)){	
				//def CopyFromFolder= config.BuildAvailability.'**'.find { Server -> Server['@Desc'] == builddesc}['@NetworkFolderPath'].toString()
				//config.BuildAvailability.'**'.find { Server -> Server['@Desc'] == builddesc}['@Job'].toString()
				//Copy a build from another location
				//RoboCopy(CopyFromFolder,BuildOutloc)
			}
		}
	   }
      }
    }
	 parallel parallelExec
}
/*
.DownloadByHttpReq Method 
.Scope: 
.
*/
def DownloadByHttpReq(outputfileLoc,BuildUrl)
{
 httpRequest ignoreSslErrors: true, outputFile: outputfileLoc, responseHandle: 'NONE', url: BuildUrl		
}	

/*
.fileUnZipOperation Method 
.Scope: 
.
*/
def fileUnZipOperation(path,BuildOutputLoc)
{
fileOperations([fileUnZipOperation(filePath: path, targetLocation: BuildOutputLoc)])
}

def RoboCopy(CopyFromFolder,CopytoFolder)
{
	//Copy File and folder /* This step only for PACS Server Setup*
	bat label: '', script: "((robocopy \"${CopyFromFolder}\" ${CopytoFolder} /S /MT:100 > C:\\log.txt) ^& IF %ERRORLEVEL% LEQ 4 exit /B 0)"
	
}
