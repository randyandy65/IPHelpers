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
		this.DebugMode = false;
		if(gs.getProperty("solvinity.enable.debug.ipcheck") =="true"){
			this.DebugMode = true;
			gs.log("IPHelper in Debug mode. Switch off in property: solvinity.enable.debug.ipcheck ");
		}
		
		this._setBase();
		this._setIpVersion();
		this._setIpSort();
		this._validateIP();
		this._setDeflateIP();
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
		this._setGateWay();
		return this.gateway;
	},
	getBaseIP_deflated: function(){
		return this.baseIP_deflated;
	},
	getFullIP_deflated: function(){
		return this.fullIP_deflated;
	},
	
	_validateIP: function(){
		if(this.ipversion!='IPv6'){
			if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(this.baseIP)) {
				this.validIP = true;
			}
		}else{
			if (/^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\-]*[A-Za-z0-9])$|^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/.test(this.baseIP)) {
				this.validIP = true;
			}
		}
	},
	
	/* Will return a valid sort value for given IP-address. Wil only except stem */
	_setIpSort: function(){
		
		if(this.ipversion=='IPv6'){ //Ipv6
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
			
			var parts = newip.split(':');
			for(i=0; i < parts.length; i++) {
				while(parts[i].length < 4) {
					parts[i] = '0' + parts[i];
				}
			}
			
			this.ipsort=parts.join('');
			
			
		}else if(this.ipversion=='IPv4'){ //IPv4 address
			if(this.DebugMode) gs.log('Sort IPv4 baseip = ' + this.baseIP);
				var networkip =  this.baseIP.split(".");
			for(i = 0; i < networkip.length; i++) {
				while(networkip[i].length < 3) {
					networkip[i] = '0' + networkip[i];
					if(this.DebugMode) gs.log('networkip[' + i + "]" + networkip[i]);
					}
			}
			this.ipsort = networkip.join('');
		}
	},
	
	_setBase: function(){
		if(this.DebugMode) gs.log("fullip = " + this.fullip);
			
		if(this.fullip.indexOf("/")>0){
			var varstem = this.fullip.split("/");
			this.baseIP = varstem[0];
			this.subnetrange = varstem[1].toString().trim();
		}else{
			this.baseIP = this.fullip;
			this.subnetrange="";
		}
	},
	
	_setIpVersion: function(){
		if(this.baseIP.indexOf(":")>0){
			this.ipversion="IPv6";
		}else if(this.baseIP.indexOf(".")>0){
			this.ipversion="IPv4";
		}
	},
	
	/*Samples:
	"/64"--> this.netmask = "ffff:ffff:ffff:ffff:0:0:0:0";
	"/112" --> this.netmask =  "ffff:ffff:ffff:ffff:ffff:ffff:ffff:0";
 	*/
	_netmaskipv6: function(){
		if(this.ipversion == 'IPv6' && this.subnetrange != ""){
			var bitstring ="";
			for (b=0; b<=(parseInt(this.subnetrange,10) - 1);b++){
				bitstring += "1";
			}
			if(this.DebugMode) gs.log("biststring = " + bitstring );
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
			if(this.DebugMode) gs.log("Getway for subnet = " + this.subnetrange.toString());
				
			if(this.subnetrange.toString() == '112'){
				if(this.DebugMode) gs.log("Getway for subnet = " + this.subnetrange);
					if(this.baseIP.substring((this.baseIP.length-2)) == ":0" ) {
					this.gateway = this.baseIP.substring(0,(this.baseIP.length-1))+"1";
				}else{
					this.gateway = this.baseIP + "1";
				}
			}else if(this.subnetrange.toString() == "64"){
				this.gateway = this.baseIP.toString() + "1";
			}else{
				this.gateway = "";
			}
		},
		
		// Remove leading '0' from ipv6 address
		// Will set this.baseIP_deflated i.e. 2a00:1558:3203:13::
		//  Will set this.fullIP_deflated i.e. 2a00:1558:3203:13::/64
		// For IPv4 it will not change anything
		_setDeflateIP: function() {
			if(this.ipversion == "IPv6"){
				if(this.DebugMode )
					gs.log("Deflate IPv6" , "IPCheck");
				
				var arrIP = this.baseIP.split(':');
				for (var i = 0; i < arrIP.length; i++) {
					if(!isNaN(parseInt(arrIP[i], 16))) {
						arrIP[i] = parseInt(arrIP[i], 16);
						arrIP[i] = this._DecToHex(arrIP[i]);
						
					}
					
					if(this.DebugMode) 
						gs.debug("arrIP[i] = " + arrIP[i] );

				}
				
				if(this.DebugMode )
					gs.log("Deflate IPv6" , "IPCheck");

				var ip = arrIP.join(':');
								
				if(this.DebugMode )
					gs.log("Deflate IPv6 Reverse IP=" + ip , "IPCheck");
								
				this.baseIP_deflated = ip;
				if (this.subnetrange != "" ) {
					this.fullIP_deflated = ip + '/' + this.subnetrange;
				}else{
					this.fullIP_deflated = ip;
				}
			}else{
				this.baseIP_deflated = this.baseIP;
				this.fullIP_deflated = this.fullip;
			}
			
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

/* 
Testscripts
var testaddress = ["2a02:10:0:1::23:10::/64",
"2a04:9a04:18a0:8a00::/112",
"2001:978:2:2c::59:0/112",
"2a0f:3506:4c:3231::/64",
"2a0f:3506:4c:3230::1:0",
"192.168.250.248/29",
"2a00:1558:1801:0004::/64"] ;

for(var tr=0; tr < testaddress.length; tr++){
    gs.print("Test run: " + tr);
    runtest(testaddress[tr]);
}


function runtest(ipaddress){
	ipcheck = new IPHelper(ipaddress);
	gs.print("=============================");
	gs.print("Testing IP Address: "+ ipaddress);
	gs.print("Valid? "+ ipcheck.isValidIP());
	gs.print("Version? "+ ipcheck.getIPVersion());
	gs.print("Base? "+ ipcheck.getBaseIP() );
	gs.print("SortIP? "+ ipcheck.getSortIP() );
	gs.print("SubnetRange? "+ ipcheck.getSubnetRange() );
	gs.print("Netmask? "+ ipcheck.getNetmask() );
	gs.print("Gateway? "+ ipcheck.getGateway() );
	gs.print("Deflated IP address: "+ ipcheck.getBaseIP_deflated() );
	gs.print("Deflated Full IP address:" + ipcheck.getFullIP_deflated() );
	gs.print("=============================");
}
 */