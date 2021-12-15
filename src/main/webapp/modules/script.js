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

    var header = null
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
            configureUIToGenerateSchedule(header, roomsCSV, classesCSV)
        }
    }

    var classesReader = new FileReader();
        classesReader.readAsText(classesFile);
        classesReader.onload = function(event){
            var csv = event.target.result;

            const parsed = parse(csv)
            header = parsed[0][0].split(';')
            classesCSV = stringify(parsed)
            console.log("classes were parsed out");

            if (classesCSV != null && roomsCSV != null) {
                configureUIToGenerateSchedule(header, roomsCSV, classesCSV)
            }
    }
}

function formatTime(time) {
    return time[3] + ':' + time[4]
}

function formatDate(date) {
    return date[2] + '/' + date[1] + '/' + date[0]
}

function appendData(data) {
    var mainContainer = document.getElementById("scheduleResult");
    for (var i = 0; i < data.length; i++) {
        var div = document.createElement("div");
        var claz = data[i].studentClass
        div.innerHTML = claz.course + ' ' + claz.executionUnit + ' ' + claz.shift + ' ' + claz.classIdentifier +
          ' '+ claz.subscribersCount + ' ' + 'turnos com capac. superior...' + ' ' + 'turno com inscrições superiores...' +
            ' '+ data[i].dayOfWeek + ' ' + formatTime(data[i].startTime) + ' ' + formatTime(data[i].startTime) +
            ' '+ formatDate(data[i].startTime) + ' ' + 'Características da sala pedida...' + ' ' + data[i].room.name +
            ' '+ data[i].room.normalCapacity + ' ' + data[i].room.normalCapacity + ' ' + data[i].room.features;

        mainContainer.appendChild(div);
    }
}

function arrayToTable(header, data) {
    var table = $('<table></table>');
    var headerRow = $('<tr></tr>');

    for (var i = 0; i < header.length; i++) {
        headerRow.append($('<td>'+ header[i] + '</td>'));
    }
    tableRow.append(header)

    for (var i = 0; i < data.length; i++) {
        var row = $('<tr></tr>');
        var claz = data[i].studentClass

        var cells = [claz.course, claz.executionUnit, claz.shift, claz.classIdentifier, claz.subscribersCount,
         'turnos com capac. superior...', 'turno com inscrições superiores...', data[i].dayOfWeek,
         formatTime(data[i].startTime), formatTime(data[i].startTime), formatDate(data[i].endTime),
          'Características da sala pedida...', data[i].room.name, data[i].room.normalCapacity, data[i].room.normalCapacity, data[i].room.features];

        for (var j =0; j < cells.length; j++) {
            row.append($('<td>'+cells[j]+'</td>'));
        }
        table.append(row);
    }

    return table;
}

function configureUIToGenerateSchedule(header, roomsCSV, classesCSV) {
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

            $('body').append(arrayToTable(header, data));

//            $('#scheduleResult').text(headerLine)
//            appendData(data)
            console.log(data)
        })
    });

    $('#generateSchedule').prop('disabled', false);
}


