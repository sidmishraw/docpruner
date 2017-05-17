### DocPruner


Prunes the bad PDFs(probably scanned images of IEEE documents from IEEE Xplore) and 
moves them out of the `input_pdfs` folder and moves folders `pdf_jsons` and 
`pdf_grouped_jsons` out of the cs267_project folder so that the PDF - JSON generation 
process can be started from scratch.
 
The artifact/jar (executable) jar is located in [here](./out/artifacts/DocPruner_jar/DocPruner.jar)

#### Usage:
```
java -jar path_to_DocPruner.jar <path-to-pdfprocessor.log> <path-to-pdf_jsons> <path-to-pdf_grouped_jsons>
```

 
Incase of concerns contact: sidharth.mishra@sjsu.edu