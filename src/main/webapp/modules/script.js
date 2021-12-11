import jQuery from "./jquery.js";
import $ from "./jquery.js";

import { parse } from './csv.js'
import { stringify } from './csv.js'

$(document).ready(function() {
    const uploadForm = document.querySelector('.upload')
    uploadForm.addEventListener('submit', function(e) {

        console.log("submitting file")

        e.preventDefault()
        let file = e.target.uploadFile.files[0]

        handleFile(e)
    })
  });

function handleFile(event) {
    console.log("submitting file")
//    var files = event.target.files
    var file = event.target.uploadFile.files[0]

    var fileInfo = `
      <span style="font-weight:bold;">${escape(file.name)}</span><br>
      - FileType: ${file.type || 'n/a'}<br>
      - FileSize: ${file.size} bytes<br>
      - LastModified: ${file.lastModifiedDate ? file.lastModifiedDate.toLocaleDateString() : 'n/a'}
    `;
    $('#file-info').append(fileInfo);

    var reader = new FileReader();
    reader.readAsText(file);
    reader.onload = function(event){
      var csv = event.target.result;

      const parsed = parse(csv)
      console.log(parsed);

      const stringified = stringify(parsed)

      $('#result').empty();
      $('#result').html(stringified);
//      $('#result').html(JSON.stringify(data, null, 2));
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