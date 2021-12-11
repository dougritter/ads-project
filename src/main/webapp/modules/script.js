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
        roomsCSV = parsed
        console.log(parsed);

        if (classesCSV != null && roomsCSV != null) {
            $('#generateSchedule').prop('disabled', false);
        }
//      const stringified = stringify(parsed)

//      $('#result').empty();
//      $('#result').html(stringified);
    }

    var classesReader = new FileReader();
        classesReader.readAsText(classesFile);
        classesReader.onload = function(event){
            var csv = event.target.result;

            const parsed = parse(csv)
            classesCSV = parsed
            console.log(parsed);

            if (classesCSV != null && roomsCSV != null) {
              $('#generateSchedule').prop('disabled', false);
            }

//            const stringified = stringify(parsed)

//            $('#result').empty();
//            $('#result').html(stringified);
    }
}

//fetch('http://localhost:3000/users', {
//   method: "POST",
//   headers: {
//      "Content-Type": "application/json",
//      "Accept": "application/json"
//   },
//   body: JSON.stringify({
//      name: name,
//   })
//})
//.then(resp => resp.json())
//.then(data => {
//   // do something here
//})