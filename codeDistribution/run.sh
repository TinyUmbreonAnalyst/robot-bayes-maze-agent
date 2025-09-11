#!/bin/bash

function echoUsage() {
    echo "Usage: ./run.sh [filename] ([move probability=".9"] [sensor_probability=".9"] [map_is_known="known"] [movment_setting="automatic"] [input_delay=500])"
}
function run() {
    arguments=("$@")
    ##echo {"${arguments[@]}","${arguments[1]}","${arguments[2]}", "${arguments[3]}", "${#arguments[@]}"}
    first_params=("-cp" ".\Server" "BayesWorld" "${arguments[0]}" "${arguments[1]}" "${arguments[2]}" "${arguments[3]}")
    ##echo "${first_params[@]}"
    (trap 'kill 0' SIGINT; java "${first_params[@]}" &)
    sleep 1
    echo "Press enter to begin navigating"
    read input
    java -cp ".\Robot" "theRobot" "${arguments[4]}" "${arguments[5]}" 
}
if [ $# -lt 1 ]; then
    echo $(echoUsage)
else
    defaults=("$1" ".9" ".9" "known" "automatic" "500")
    i=2
    while [ $i -lt $# ]; do
        defaults[$i]="${!($i-1)}"
        ((i++))
    done
    run "${defaults[@]}" 
fi

