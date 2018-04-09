/* For now only IPv6 functionality */
var IPHelper = Class.create();
IPHelper.prototype = {
	initialize: function(curIP) {
		this.fullip = curIP;
		this.baseIP = '';
		this.ipversion = '';
		this.ipsort = '';
		this.validIP = false;
		this.netmask = "";
		this.subnetrange = "";
		this.gateway = "";
		this.baseIP_deflated = "";
		this.fullIP_deflated = "";
		this.ipparts = {};
		this.log = new GSLogExt('integration.log_level' , 'IPHelper()');
		this.DebugMode = false;
		if(gs.getProperty("solvinity.enable.debug.ipcheck") =="true"){
			this.DebugMode = true;
			this.log.pushDebug("IPHelper in Debug mode. Switch off in property: solvinity.enable.debug.ipcheck ");
		}
		this.log.pushDebug("IPHelper init");
		this._setBase();
		this._setIpVersion();
		this._validateIP();
		if(this.validIP){
			this._setIpSort();
			this._setDeflateIP();
			this._setGateWay();
		}
		if(this.DebugMode){
			this.log.logDebug(" - IPHelper init");
		}
	},
	
	getBaseIP: function(){
		return this.baseIP;
	},
	getIPVersion: function(){
		return this.ipversion;
	},
	getSortIP: function(){
		return this.ipsort;
	},
	getSubnetRange: function(){
		return this.subnetrange;
	},
	isValidIP: function(){
		return this.validIP;
	},
	getNetmask: function(){
		this._netmaskipv6();
		return this.netmask;
	},
	getNetmaskv6: function(){
		return this._netmaskipv61();
	},
	getGateway: function(){
		
		return this.gateway;
	},
	getBaseIP_deflated: function(){
		return this.baseIP_deflated;
	},
	getFullIP_deflated: function(){
		return this.fullIP_deflated;
	},
	
	_validateIP: function(){
		this.log.pushDebug("validateIP()");
		if(this.ipversion!='IPv6'){
			if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(this.baseIP)) {
				this.validIP = true;
			}
		}else{
			if (/^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/.test(this.baseIP)) {
				this.validIP = true;
			}
		}
		this.log.pushDebug("   Valid? " + this.validIP );
		this.log.pushDebug(" - validateIP()");
	},
	
	/* Will return a valid sort value for given IP-address. Will only except stem */
	_setIpSort: function(){
		this.log.pushDebug("setIPSort()");
		if(this.ipversion=='IPv6'){ //Ipv6
			this.log.pushDebug(" -- IPV6");
			var newip =  this.baseIP;
			var curSplit = this.baseIP.split(":");
			var ceros="";
			var mispart = 8 - curSplit.length;
			if(mispart > 0){
				for(c=0; c<= mispart; c++){
					ceros += ":0000";
				}
				ceros +=":";
				newip = this.baseIP.replace('::', ceros);
			}
			
			this.ipparts = newip.split(':');
			for(i=0; i < this.ipparts.length; i++) {
				while(this.ipparts[i].length < 4) {
					this.ipparts[i] = '0' + this.ipparts[i];
				}
			}
			
			this.ipsort=this.ipparts.join('');
			
			
		}else if(this.ipversion=='IPv4'){ //IPv4 address
			this.log.pushDebug(" -- IPV4");
			
			var networkip =  this.baseIP.split(".");
			for(i = 0; i < networkip.length; i++) {
				while(networkip[i].length < 3) {
					networkip[i] = '0' + networkip[i];
					if(this.DebugMode) gs.log('networkip[' + i + "]" + networkip[i]);
					}
			}
			this.ipsort = networkip.join('');
		}
		this.log.pushDebug(" - setIPSort()");
	},
	
	_setBase: function(){
		this.log.pushDebug("setBaseIP()");
		this.log.pushDebug(" -- fullip = " + this.fullip);
		if(this.fullip.indexOf("/")>0){
			var varstem = this.fullip.split("/");
			this.baseIP = varstem[0];
			this.subnetrange = varstem[1].toString().trim();
		}else{
			this.baseIP = this.fullip;
			this.subnetrange="";
		}
		this.log.pushDebug(" - setBaseIP()");
	},
	
	_setIpVersion: function(){
		this.log.pushDebug("setIpVersion()");
		if(this.baseIP.indexOf(":")>0){
			this.ipversion="IPv6";
		}else if(this.baseIP.indexOf(".")>0){
			this.ipversion="IPv4";
		}
		this.log.pushDebug(" - setIpVersion()");
	},
	
	/*Samples:
	"/64"--> this.netmask = "ffff:ffff:ffff:ffff:0:0:0:0";
	"/112" --> this.netmask =  "ffff:ffff:ffff:ffff:ffff:ffff:ffff:0";
 	*/
	_netmaskipv6: function(){
		this.log.pushDebug("Set Netmask _netmaskipv6()");
		if(this.ipversion == 'IPv6' && this.subnetrange != ""){
			
			var bitstring ="";
			for (b=0; b<=(parseInt(this.subnetrange,10) - 1);b++){
				bitstring += "1";
			}
			this.log.pushDebug(" - biststring = " + bitstring );
			
			for (c = parseInt(this.subnetrange,10); c <= 127 ;c++){
				bitstring += "0";
			}
			var bytes={};
				var blok = 0;
				var ast = "";
				//Get 8x 4 bytes
				for(d=0; d<=32; d++ ){
					bytes[d] = "";
					ast = bitstring.toString().substring(blok,blok+4) ;
					bytes[d] = bitstring.substring(blok,blok+4) ;
					blok = blok + 4;
				}
				var startbyte = 0;
				//join the bytes in Hex words
				for(w=0;w<=7;w++){
					this.netmask += this._BinToHex(bytes[startbyte]) ;
					this.netmask += this._BinToHex(bytes[startbyte+1]) ;
					this.netmask +=	this._BinToHex(bytes[startbyte+2]);
					this.netmask += this._BinToHex(bytes[startbyte+3]) ;
					startbyte += 4;
					if(startbyte<32){
						this.netmask += ":";
					}
				}
			}
		},
		
		/*
		De gateway bij een /64 en bij een /112 ipv6 adres eindigt bij beiden altijd op :1.
		Als er bij /112 een :0 dan de 0 vervangen met 1
		v.b. 2a00:1558:f000::4:0/112 => 2a00:1558:f000::4::1
 		*/
		_setGateWay: function(){
			this.log.pushDebug("setGateWay()")
			this.log.pushDebug("  using deflate ip = " + this.baseIP_deflated.toString())
			this.log.pushDebug("  getway subnet = " + this.subnetrange.toString());
				
			if(this.subnetrange.toString() == '112'){
				if(this.DebugMode) gs.log("Getway for subnet = " + this.subnetrange);
					if(this.baseIP_deflated.substring((this.baseIP_deflated.length-2)) == ":0" ) {
					this.gateway = this.baseIP_deflated.substring(0,(this.baseIP_deflated.length-1))+"1";
				}else{
					this.gateway = this.baseIP_deflated + "1";
				}
			}else if(this.subnetrange.toString() == "64"){
				this.gateway = this.baseIP_deflated.toString() + "1";
			}else{
				this.log.pushDebug("  no subnet! " );
				this.gateway = "";
			}
			this.log.pushDebug(" - setGateWay()" );
		},
		
		// Remove leading '0' from ipv6 address
		// Will set this.baseIP_deflated i.e. 2a00:1558:3203:13::
		//  Will set this.fullIP_deflated i.e. 2a00:1558:3203:13::/64
		// For IPv4 it will not change anything
		_setDeflateIP: function() {
			this.log.pushDebug("setDeflateIP()");
			if(this.ipversion == "IPv6"){
				this.log.pushDebug(" - ip version 6");
				
				var ip = ""
				//here we will join the ipparts to a single inflated ip string
				//taking into account that there can be only 1 "::"
				var resultip = "";
				var deflated = false;
				//instead of using slice() we copy the IP parts but converted for later use
				var arrIP = {};
				for (var pp=0; pp<=this.ipparts.length - 1; pp += 1){
					arrIP[pp] = parseInt(this.ipparts[pp], 16);
					arrIP[pp] = this._DecToHex(arrIP[pp]).toString();
				}
				
				for (var pp=0; pp<=this.ipparts.length - 1; pp += 1){
					this.log.pushDebug(" - part = " + arrIP[pp]);
					if(arrIP[pp] == "0" && !deflated && arrIP[pp+1]=="0"){
						//find all cero's and deflate
						for(cc=pp; cc<this.ipparts.length - 1; cc += 1){
							if(arrIP[cc]!="0")	break;
						}
						pp = cc ;
						deflated = true;
						resultip += ":";
						//Last number could be a cero, so dont add
						resultip +=  (pp == 7 ? "" : arrIP[pp] ) + (pp == 7 ? "" : ":"); 
					}else{
						resultip += arrIP[pp] + (pp == 7 ? "" : ":"); 
					}
				}
					
								
				this.baseIP_deflated = resultip;
				if (this.subnetrange != "" ) {
					this.fullIP_deflated = resultip + '/' + this.subnetrange;
				}else{
					this.fullIP_deflated = resultip;
				}
			}else{
				this.log.pushDebug(" - ip version 4 ");
				this.baseIP_deflated = this.baseIP;
				this.fullIP_deflated = this.fullip;
			}
			this.log.pushDebug(" - setDeflateIP()");
		},
		
		_reverseString: function(str) {
			var temp = '';
			var i = str.length;
			while(i > 0) {
				temp += str.substring(i - 1, i);
				i--;
			}
			return temp;
		},
		
		
		/* Conversion helpers */
		_BinToHex: function(binary){
			return parseInt(binary,2).toString(16);
		},
		_HexToBin: function(hexval){
			return parseInt(hexval,16).toString(2);
		},
		_BinToDec: function(binary){
			return parseInt(binary,2).toString(10);
		},
		_DecToBin: function(dec){
			return dec.toString(2);
		},
		_DecToHex: function(dec){
			return dec.toString(16);
		},
		
		_HexToDec: function(hexnumber){
			var cc = this._HexToBin(hexnumber);
			return this._BinToDec(cc);
		},
	
	type: 'IPHelper'
} ;
