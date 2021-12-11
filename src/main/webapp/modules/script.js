import jQuery from "./jquery.js";
import $ from "./jquery.js";

import { parse } from './csv.js'
import { stringify } from './csv.js'

$(document).ready(function() {
    const uploadForm = document.querySelector('.upload')
    uploadForm.addEventListener('submit', function(e) {
        console.log("submitting files")
        e.preventDefault()

        handleFiles(e)
    })
 });

function getFileInfo(file) {
    return `
         <span style="font-weight:bold;">${escape(file.name)}</span><br>
         - FileType: ${file.type || 'n/a'}<br>
         - FileSize: ${file.size} bytes<br>
         - LastModified: ${file.lastModifiedDate ? file.lastModifiedDate.toLocaleDateString() : 'n/a'}
        `;
}

function handleFiles(event) {
    console.log("handling files")
    var roomsFile = event.target.uploadRooms.files[0]
    var classesFile = event.target.uploadClasses.files[0]

    $('#rooms-file-info').append(getFileInfo(roomsFile))
    $('#classes-file-info').append(getFileInfo(classesFile))

    var roomsCSV = null
    var classesCSV = null

    var reader = new FileReader();
    reader.readAsText(roomsFile);
    reader.onload = function(event){
        var csv = event.target.result;

        const parsed = parse(csv)
        roomsCSV = stringify(parsed)
        console.log("rooms were parsed out");

        if (classesCSV != null && roomsCSV != null) {
            configureUIToGenerateSchedule(roomsCSV, classesCSV)
        }

    }

    var classesReader = new FileReader();
        classesReader.readAsText(classesFile);
        classesReader.onload = function(event){
            var csv = event.target.result;

            const parsed = parse(csv)
            classesCSV = stringify(parsed)
            console.log("classes were parsed out");

            if (classesCSV != null && roomsCSV != null) {
                configureUIToGenerateSchedule(roomsCSV, classesCSV)
            }
    }
}

function configureUIToGenerateSchedule(roomsCSV, classesCSV) {
    if ($('#generateSchedule').prop('disabled') == false) {
        console.log("already set up UI")
        return
    }

    let url = window.location.href + "upload-csv"

    $('#generateSchedule').click(function(){
        fetch(url, {
           method: "POST",
           headers: {
              "Content-Type": "application/json",
              "Accept": "application/json"
           },
           body: JSON.stringify({
              rooms: roomsCSV, classes: classesCSV
           })
        })
        .then(resp => resp.json())
        .then(data => {
            console.log("generated schedule")
            console.log(data[0])
        })
    });

    $('#generateSchedule').prop('disabled', false);
}

