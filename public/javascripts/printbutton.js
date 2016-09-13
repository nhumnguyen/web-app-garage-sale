function printpage() {
  var printButton = document.getElementById("printpagebtn");
  printButton.style.visibility = 'hidden';
  window.print()
  printButton.style.visibility = 'visible';
}
