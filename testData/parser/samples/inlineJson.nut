function setLed(config) {
    if (config.state == "on") print("on")
    else print("off")
}

setLed({"state" : "on", "array" : [{"nested" : 1, "bool" : true}]})