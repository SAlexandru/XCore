#! /bin/bash

pdflatex main.tex
bibtex main
makeglossaries main
pdflatex main.tex
pdflatex main.tex
pdflatex -halt-on-error main.tex

rm -rf main.aux  main.bbl  main.blg  main.lof  main.log  main.lot  main.toc main.out
mv main.pdf output/main.pdf

open output/main.pdf
