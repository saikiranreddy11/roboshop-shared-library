#!groovy
def call(Map configMap){
   def application_type = configMap.get("application_type")
    def component = configMap.get("component")
    switch(application_type){
        case 'nodeJSVMCI':
                    nodeJSVMCI(configMap) //if the function name is call() in nodeJSVMCI, it is automatically called
                    break
        case 'javaVM':
                    javaVM(configMap)
                    break
        default :
                echo "Error: enter the correct application type"
                break
    }
}