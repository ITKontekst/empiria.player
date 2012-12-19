(function(){var a,k,c;a=jQuery;k=function(){this.fileLoadingImage="../images/loading.gif";this.fileCloseImage="../images/close.png";this.resizeDuration=700;this.fadeDuration=500;this.labelImage="Image";this.labelOf="of"};c=function(b){this.options=b;this.album=[];this.currentImageIndex=void 0;this.handlers=[];this.build()};c.prototype.build=function(){if(!a("#lightbox")){var b,d=this;a("<div>",{id:"lightboxOverlay"}).after(a("<div/>",{id:"lightbox"}).append(a("<div/>",{"class":"lb-outerContainer"}).append(a("<div/>",
{"class":"lb-container"}).append(a("<img/>",{"class":"lb-image"}),a("<div/>",{"class":"lb-nav"}).append(a("<a/>",{"class":"lb-prev"}),a("<a/>",{"class":"lb-next"})),a("<div/>",{"class":"lb-loader"}).append(a("<a/>",{"class":"lb-cancel"}).append(a("<img/>",{src:this.options.fileLoadingImage}))))),a("<div/>",{"class":"lb-dataContainer"}).append(a("<div/>",{"class":"lb-data"}).append(a("<div/>",{"class":"lb-details"}).append(a("<span/>",{"class":"lb-caption"}),a("<span/>",{"class":"lb-number"})),a("<div/>",
{"class":"lb-closeContainer"}).append(a("<a/>",{"class":"lb-close"}).append(a("<img/>",{src:this.options.fileCloseImage}))))))).appendTo(a("body"));a("#lightboxOverlay").hide().on("click",function(){d.end();return!1});b=a("#lightbox");b.hide().on("click",function(){d.end();return!1});b.find(".lb-outerContainer").on("click",function(){d.end();return!1});b.find(".lb-prev").on("click",function(){d.changeImage(d.currentImageIndex-1);return!1});b.find(".lb-next").on("click",function(){d.changeImage(d.currentImageIndex+
1);return!1});b.find(".lb-loader, .lb-close").on("click",function(){d.end();return!1})}};c.prototype.add=function(b,a){this.album.push({link:b,title:a})};c.prototype.clear=function(){this.album=[]};c.prototype.startSingle=function(){a(window).on("resize",this.sizeOverlay);a("select, object, embed").css({visibility:"hidden"});a("#lightboxOverlay").width(a(document).width()).height(a(document).height()).fadeIn(this.options.fadeDuration);this.prepareView();this.changeImage(0)};c.prototype.startDefault=
function(b){var d,c,e,f;a(window).on("resize",this.sizeOverlay);a("select, object, embed").css({visibility:"hidden"});a("#lightboxOverlay").width(a(document).width()).height(a(document).height()).fadeIn(this.options.fadeDuration);c=e=0;for(f=this.album.length;c<f;c++)if(d=this.album[c],a(d).attr("href")===b.attr("href")){e=c;break}this.prepareView();this.changeImage(e)};c.prototype.start=function(b){0==arguments.length?this.startSingle():this.startDefault(b);this.callHandlers({isInFullScreen:!0})};
c.prototype.prepareView=function(){var b,d;b=a(window);d=b.scrollTop()+b.height()/10;b=b.scrollLeft();a("#lightbox").css({top:d+"px",left:b+"px"}).fadeIn(this.options.fadeDuration)};c.prototype.changeImage=function(b){var d,c,e,f=this;this.disableKeyboardNav();c=a("#lightbox");d=c.find(".lb-image");this.sizeOverlay();a("#lightboxOverlay").fadeIn(this.options.fadeDuration);a(".loader").fadeIn("slow");c.find(".lb-image, .lb-nav, .lb-prev, .lb-next, .lb-dataContainer, .lb-numbers, .lb-caption").hide();
c.find(".lb-outerContainer").addClass("animating");e=new Image;e.onload=function(){d.attr("src",f.album[b].link);d.width=e.width;d.height=e.height;return f.sizeContainer(e.width,e.height)};e.src=this.album[b].link;this.currentImageIndex=b};c.prototype.sizeOverlay=function(){return a("#lightboxOverlay").width(a(document).width()).height(a(document).height())};c.prototype.sizeContainer=function(b,c){var g,e,f,k,n,p,h,j,l,m,q=this;e=a("#lightbox");f=e.find(".lb-outerContainer");m=f.outerWidth();l=f.outerHeight();
g=e.find(".lb-container");p=parseInt(g.css("padding-top"),10);n=parseInt(g.css("padding-right"),10);k=parseInt(g.css("padding-bottom"),10);g=parseInt(g.css("padding-left"),10);j=b+g+n;h=c+p+k;j!==m&&h!==l?f.animate({width:j,height:h},this.options.resizeDuration,"swing"):j!==m?f.animate({width:j},this.options.resizeDuration,"swing"):h!==l&&f.animate({height:h},this.options.resizeDuration,"swing");setTimeout(function(){e.find(".lb-dataContainer").width(j);e.find(".lb-prevLink").height(h);e.find(".lb-nextLink").height(h);
q.showImage()},this.options.resizeDuration)};c.prototype.showImage=function(){var b;b=a("#lightbox");b.find(".lb-loader").hide();b.find(".lb-image").fadeIn("slow");this.updateNav();this.updateDetails();this.preloadNeighboringImages();this.enableKeyboardNav()};c.prototype.updateNav=function(){var b;b=a("#lightbox");b.find(".lb-nav").show();0<this.currentImageIndex&&b.find(".lb-prev").show();this.currentImageIndex<this.album.length-1&&b.find(".lb-next").show()};c.prototype.updateDetails=function(){var b,
c=this;b=a("#lightbox");"undefined"!==typeof this.album[this.currentImageIndex].title&&""!==this.album[this.currentImageIndex].title&&b.find(".lb-caption").html(this.album[this.currentImageIndex].title).fadeIn("fast");1<this.album.length?b.find(".lb-number").html(this.options.labelImage+" "+(this.currentImageIndex+1)+" "+this.options.labelOf+"  "+this.album.length).fadeIn("fast"):b.find(".lb-number").hide();b.find(".lb-outerContainer").removeClass("animating");b.find(".lb-dataContainer").fadeIn(this.resizeDuration,
function(){return c.sizeOverlay()})};c.prototype.preloadNeighboringImages=function(){var b;this.album.length>this.currentImageIndex+1&&(b=new Image,b.src=this.album[this.currentImageIndex+1].link);0<this.currentImageIndex&&(b=new Image,b.src=this.album[this.currentImageIndex-1].link)};c.prototype.enableKeyboardNav=function(){a(document).on("keyup.keyboard",a.proxy(this.keyboardAction,this))};c.prototype.disableKeyboardNav=function(){a(document).off(".keyboard")};c.prototype.keyboardAction=function(b){var a;
a=b.keyCode;b=String.fromCharCode(a).toLowerCase();27===a||b.match(/x|o|c/)?this.end():"p"===b||37===a?0!==this.currentImageIndex&&this.changeImage(this.currentImageIndex-1):("n"===b||39===a)&&this.currentImageIndex!==this.album.length-1&&this.changeImage(this.currentImageIndex+1)};c.prototype.end=function(){this.disableKeyboardNav();a(window).off("resize",this.sizeOverlay);a("#lightbox").fadeOut(this.options.fadeDuration);a("#lightboxOverlay").fadeOut(this.options.fadeDuration);this.callHandlers({isInFullScreen:!1});
return a("select, object, embed").css({visibility:"visible"})};c.prototype.callHandlers=function(a){var c;i=0;for(_len=this.handlers.length;i<_len;i++){c=this.handlers[i];try{c.handle(a)}catch(g){}}};c.prototype.addListener=function(a){this.handlers.push(a)};a(function(){window.Lightbox=c;window.LightboxOptions=k})})();