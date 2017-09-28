/**
 * Diese Datei ist Teil des Alexa Skills Rollenspiel Soloabenteuer.
 * Copyright (C) 2016-2017 Ferenc Hechler (github@fh.anderemails.de)
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer ist Freie Software: 
 * Sie koennen es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder spaeteren
 * veroeffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer wird in der Hoffnung, 
 * dass es nuetzlich sein wird, aber
 * OHNE JEDE GEWAEHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewaehrleistung der MARKTFAEHIGKEIT oder EIGNUNG FUER EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License fuer weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

/**
 * App ID for the skill
 */
// SOLOABENTEUER
var APP_ID = process.env.APP_ID; //replace with "amzn1.echo-sdk-ams.app.[your-unique-value-here]";

// // SOLO
var endpoint = process.env.ENDPOINT;          // 'http://localhost:8080/RoleplayRestService/rest/solo';
var endpointDev = process.env.ENDPOINT_DEV;   // 'http://localhost:8080/DEVRoleplayRestService/rest/solo';

var URL = require('url');
var authUsername = process.env.AUTH_USERNAME;   // 'rest';
var authPassword = process.env.AUTH_PASSWORD;   // 'geheim';

// LOG environment variables
console.log("APP_ID="+APP_ID);
console.log("ENDPOINT="+endpoint);
console.log("ENDPOINT_DEV="+endpointDev);
console.log("AUTH_USERNAME="+authUsername);
console.log("AUTH_PASSWORD="+(authPassword.replace(/./g, '*')));


/**
 * The AlexaSkill prototype and helper functions
 */
var AlexaSkill = require('./AlexaSkill');

var speech = require('./Speech');
speech.init_messages("DE");

var http = require('http');
var querystring = require("querystring");



/**
 * SoloAbenteuerSkill is a child of AlexaSkill.
 */
var SoloAbenteuerSkill = function () {
    AlexaSkill.call(this, APP_ID);
};

// Extend AlexaSkill
SoloAbenteuerSkill.prototype = Object.create(AlexaSkill.prototype);
SoloAbenteuerSkill.prototype.constructor = SoloAbenteuerSkill;

SoloAbenteuerSkill.prototype.eventHandlers.onSessionStarted = function (sessionStartedRequest, session) {
	console.log("onSessionStarted requestId: " + sessionStartedRequest.requestId + ", sessionId: " + session.sessionId);
    // any initialization logic goes here
    clearSessionData(session);
    setPhase("init", session);
};

SoloAbenteuerSkill.prototype.eventHandlers.onLaunch = function (launchRequest, session, response) {
	console.log("onLaunch requestId: " + launchRequest.requestId + ", sessionId: " + session.sessionId);
	return execReconnectGame(session, response);
};

SoloAbenteuerSkill.prototype.eventHandlers.onSessionEnded = function (sessionEndedRequest, session) {
    console.log("SoloAbenteuerSkill onSessionEnded requestId: " + sessionEndedRequest.requestId
        + ", sessionId: " + session.sessionId);
    // any cleanup logic goes here
    clearSessionData(session);
};

SoloAbenteuerSkill.prototype.intentHandlers = {
		
    "AnswerIntent": execAnswerIntent,
    
    "StartSoloIntent": execStartSoloIntent,
	    
    "ImportUploadIntent": execImportUploadIntent,
    
    "RepeatIntent": execRepeatIntent,

    "UserConfigActivateIntent": execUserConfigActivateIntent,

    "UserConfigDeactivateIntent": execUserConfigDeactivateIntent,
    
    "AMAZON.StartOverIntent": function (intent, session, response) {
    	setConfirmation(intent.name, session);
        speech.respond(intent.name, "CONFIRM", response);
    },

    "AMAZON.StopIntent": function (intent, session, response) {
        clearSessionData(session);
    	speech.goodbye(intent.name, "*", response);
    },

    "AMAZON.HelpIntent": function (intent, session, response) {
    	setLongHelp(session);
    	phaseHelp("", session, response);
    },
    

    "AMAZON.YesIntent": execYesIntent,
    
    "AMAZON.NoIntent": execNoIntent

    
};

// Create the handler that responds to the Alexa Request.
exports.handler = function (event, context) {
    // Create an instance of the SoloAbenteuerSkill skill.
    var soloAbenteuerSkill = new SoloAbenteuerSkill();
    soloAbenteuerSkill.execute(event, context);
};


function execConnectUser(session, response, successCallback) {
    sendCommand(session, "", "connect", session.user.userId, "", function callbackFunc(connectResult) {
    	if (connectResult.code === "S_OK") {
            console.log("connect to GameId: " + connectResult.gameId);
        	setSessionGameId(session, connectResult.gameId);
        	setPhase("play", session);
        	successCallback();
    	}
    	else {
        	speech.respond("LAUNCH", connectResult.code, response, connectResult.errmsg);
    	}
    });
}



function execReconnectGame(session, response) {
// DEBUG:
//	console.log("EXECRECONNECTGAME: "+JSON.stringify(session));    	
    var oldEndpoint = getSessionEndpoint(session);
    sendCommand(session, "", "connect", session.user.userId, "", function callbackFunc(connectResult) {
// DEBUG:
//console.log("CONNECTRESULT: "+JSON.stringify(connectResult));    	
    	if (connectResult.code === "S_OK") {
            console.log("reconnect to GameId: " + connectResult.gameId);
        	setSessionFlags(session, connectResult.activeFlags);
            var newEndpoint = getSessionEndpoint(session);
        	if (newEndpoint !== oldEndpoint) {
        		execForceReconnectGame(session, response);
        	}
        	else {
	        	setSessionGameId(session, connectResult.gameId);
	        	setPhase("play", session);
	        	execRepeatIntent("RepeatIntent", session, response, 'RECONNECT');
        	}
    	}
    	else {
        	speech.respond("LAUNCH", connectResult.code, response);
    	}
    });
}

function execForceReconnectGame(session, response) {
// DEBUG:
//console.log("EXECRECONNECTGAME: "+JSON.stringify(session));    	
    var oldEndpoint = getSessionEndpoint(session);
    sendCommand(session, "", "forceConnect", session.user.userId, "", function callbackFunc(connectResult) {
// DEBUG:
//console.log("CONNECTRESULT: "+JSON.stringify(connectResult));    	
    	if (connectResult.code === "S_OK") {
            console.log("reconnect to GameId: " + connectResult.gameId);
        	setSessionFlags(session, connectResult.activeFlags);
            var newEndpoint = getSessionEndpoint(session);
        	if (newEndpoint !== oldEndpoint) {
        		execForceReconnectGame(session, response);
        	}
        	else {
	        	setSessionGameId(session, connectResult.gameId);
	        	setPhase("play", session);
	        	execRepeatIntent("RepeatIntent", session, response, 'RECONNECT');
        	}
    	}
    	else {
        	speech.respond("LAUNCH", connectResult.code, response);
    	}
    });
}



function execAnswerIntent(intent, session, response) {
	execSendAnswerIntent(getAnswer(intent), intent, session, response);
}

function execStartSoloIntent(intent, session, response) {
	execSendAnswerIntent(getSoloName(intent), intent, session, response);
}

function execSendAnswerIntent(answer, intent, session, response) {
	if (isInPhase("init", session)) {
		execConnectUser(session, response, function success() {
			if (!isInPhase("init", session)) {  // must always be true! Just to be sure, not to cause am endless recursion. 
				execSendAnswerIntent(answer, intent, session, response);
			}
		});
		return;
	}
	if (!checkPhase("play", session, response)) {
		return;
	}
	sendCommand(session, getSessionGameId(session), "answer", answer, "", function callbackFunc(result) {
        console.log(intent.name+": gid: "+ getSessionGameId(session) + ", res: "+result.code);
        if (result.code === "S_OK") {
            console.log("text: " + result.text);
        	speech.ask(result.text, response);
        }
        else if (result.code === "S_PLAYER_WINS") {
            console.log("text: " + result.text);
        	speech.tell(result.text, response);
        }
        else {
        	speech.respond(intent.name, result.code, response);
        }
    });
}

function execUserConfigActivateIntent(intent, session, response) {
	execUserConfigIntent("activateFlag", getFlagName(intent), intent, session, response);
}

function execUserConfigDeactivateIntent(intent, session, response) {
	execUserConfigIntent("deactivateFlag", getFlagName(intent), intent, session, response);
}

function execUserConfigIntent(cmd, flagName, intent, session, response) {
	if (isInPhase("init", session)) {
		execConnectUser(session, response, function success() {
			if (!isInPhase("init", session)) {  // must always be true! Just to be sure, not to cause am endless recursion. 
				execUserConfigIntent(cmd, flagName, intent, session, response);
			}
		});
		return;
	}
	if (!checkPhase("play", session, response)) {
		return;
	}
	sendCommand(session, getSessionGameId(session), cmd, flagName, "", function callbackFunc(result) {
        console.log(intent.name+": gid: "+ getSessionGameId(session) + ", res: "+result.code);
        if (result.code === "S_OK") {
            console.log("flag: " + result.value);
            speech.respond(intent.name, result.code, response, flagName);
        }
        else {
        	speech.respond(intent.name, result.code, response, flagName);
        }
    });
}


function execImportUploadIntent(intent, session, response) {
	sendCommand(session, getSessionGameId(session), "upload", session.user.userId, getGameId(intent), function callbackFunc(result) {
        console.log(intent.name+": gid: "+ getGameId(intent) + ", res: "+result.code);
        if (result.code === "S_OK") {
        	speech.tell("Das Soloabenteuer '"+result.value+"' wurde erfolgreich geladen und erscheint nun als erstes in Deiner Soloabenteuer Liste", response);
        }
        else {
        	speech.respond(intent.name, result.code, response);
        }
    });
}

function execRepeatIntent(intent, session, response, defaultPart) {
	if (!defaultPart) {
		defaultPart = "?";
	}
	if (isInPhase("init", session)) {
		execReconnectGame(session, response);
		return;
	}
//	if (!checkPhase("play", session, response)) {
//		return;
//	}
	var part = getPart(intent, defaultPart);
	if (part === '?') {
    	setConfirmation(intent.name, session);
        speech.respond(intent.name, "CONFIRM", response);
	}
	else {
		sendCommand(session, getSessionGameId(session), "repeat", part, "", function callbackFunc(result) {
	        console.log(intent.name+": gid: "+ getSessionGameId(session) + ", res: "+result.code);
	        if (result.code === "S_OK") {
	            console.log("repeat: " + result.text);
	        	speech.ask(result.text, response);
	        }
	        else {
	        	speech.respond(intent.name, result.code, response);
	        }
	    });
	}
}


function execYesIntent(intent, session, response) {
	var confirmation = getConfirmation(session, "?");
	if (confirmation === "?") {
		execSendAnswerIntent("JA", intent, session, response);
		return;
	}
	clearConfirmation(session);
	if (confirmation === "AMAZON.StopIntent") {
        console.log("CONFIRMED-"+confirmation + ": sessionId: " + session.sessionId+", result: "+result.code);
        clearSessionData(session);
        speech.goodbye(confirmation, "*", response);
        response.tellWithCard("Auf wiederhören, bis zum nächsten Mal.", "Soloabenteuer Skill", "Auf wiederhören, bis zum nächsten Mal.");
	}
	else if (confirmation === "AMAZON.StartOverIntent") {
    	sendCommand(session, getSessionGameId(session), "restart", "", "", function callbackFunc(result) {
            console.log("CONFIRMED-"+confirmation + ": gid: " + getSessionGameId(session) + ", result: "+result.code);
//            speech.respond(confirmation, result.code, response);
            execReconnectGame(session, response);
        });
	}
	else if (confirmation === "RepeatIntent") {
		execRepeatIntent(intent, session, response, "TEIL");
	}
	else {
		response.tellWithCard("Das sollte nicht passieren, unbekannte Bestätigung "+confirmation+"! Das Spiel wird beendet, sorry.", "Soloabenteuer Skill", "Das sollte nicht passieren, unbekannte Bestätigung '"+confirmation+"'! Das Spiel wird beendet, sorry.");
	}
}


function execNoIntent(intent, session, response) {
	var confirmation = getConfirmation(session, "?");
	if (confirmation === "?") {
		execSendAnswerIntent("NEIN", intent, session, response);
		return;
	}
	clearConfirmation(session);
	if (confirmation === "RepeatIntent") {
		execRepeatIntent(intent, session, response, "ALLES");
		return;
	}
    response.askWithCard("Okay, weiter gehts!", "Soloabenteuer Skill", "OK, weiter gehts!");
}



function checkPhase(comparePhase, session, response) {
	var confirmation = getConfirmation(session, "?");
	clearConfirmation(session);
	if (confirmation !== "?") {
	    response.askWithCard("Ich werte das als Nein, weiter gehts!", "Soloabenteuer Skill", "ich werte das als Nein, weiter gehts!");
	    return false;
	}
	if (!isInPhase(comparePhase, session)) {
		wrongPhaseResponse(session, response);
		return false;
	}
	return true;
}

function clearSessionData(session) {
	session.attributes = {};
}

function issueHelp(issue, session, response) {
	console.log("ISSUE="+issue);
	if (!speech.hasMsg("IssueHelpIntent", issue)) {
		issue = "*";
	}
	speech.respond("IssueHelpIntent", issue, response);
}

function wrongPhaseResponse(session, response) {
	phaseHelp("Ich habe dein Kommando nicht verstanden. ", session, response);
}

function phaseHelp(prefixMsg, session, response) {
	var phase = getPhaseFromSession(session, "?");
	var msg;
	if (phase === "init") {
		msg = speech.addMsg(prefixMsg, "PHASEHELP", "INIT");
	}
	else if (phase === "play") {
		if (isLongHelp(session)) {
			msg = speech.addMsg(prefixMsg, "PHASEHELP", "PLAY_LONG");
			// give the long version only once. 
			setShortHelp(session);
		}
		else {
			msg = speech.addMsg(prefixMsg, "PHASEHELP", "PLAY_SHORT");
		}
	}
	else {
	    console.log('UNKNOWN PHASE: ' + phase);
		msg = speech.addMsg(prefixMsg, "PHASEHELP", "UNKNOWN", phase);
		speech.goodbyeMsg(msg, response);
	    return;
	}
	speech.respondMsg(msg, response);
}


function setConfirmation(newConfirmation, session, param)  {
	session.attributes.confirmation = newConfirmation;
	session.attributes.confirmParam = param;
}
function getConfirmation(session, defaultValue)  {
	if (!session || (!session.attributes) || (!session.attributes.confirmation)) {
		return defaultValue;
	}
	return session.attributes.confirmation;
}
function getConfirmParam(session, defaultValue)  {
	if (!session || (!session.attributes) || (!session.attributes.confirmParam)) {
		return defaultValue;
	}
	return session.attributes.confirmParam;
}
function clearConfirmation(session)  {
	setConfirmation(null, session, null);
}

function setPhase(newPhase, session)  {
	session.attributes.phase = newPhase;
}
function isInPhase(comparePhase, session)  {
	var phase = getPhaseFromSession(session, "?");
	return phase.startsWith(comparePhase);
}
function getPhaseFromSession(session, defaultValue)  {
	if (!session || (!session.attributes) || (!session.attributes.phase)) {
		return defaultValue;
	}
	return session.attributes.phase;
}

function setShortHelp(session)  {
	session.attributes.shortHelp = true;
}
function setLongHelp(session)  {
	session.attributes.shortHelp = false;
}
function isShortHelp(session)  {
	if (!session || (!session.attributes) || (!session.attributes.shortHelp)) {
		return false;
	}
	return session.attributes.shortHelp === true;
}
function isLongHelp(session)  {
	return !isShortHelp(session);
}



function getFlagName(intent) {
	return getFromIntent(intent, "flag_name", "?");
}

function getAnswer(intent) {
	return getFromIntent(intent, "answer", "?");
}

function getSoloName(intent) {
	return getFromIntent(intent, "soloname", "?");
}

function getPart(intent, defaultPart) {
	return getFromIntent(intent, "part", defaultPart);
}

/**
 * handle the following flags
 * DO_NOT_WAIT_FOR_ANSWER("Diskutiermodus"),
 * SHOW_PREPARED_ADVENTURES("Testspielanzeige"),
 * SHORTEN_TEXTS("Schnelldurchlauf"),
 * ALEXA_ROLLS_DICES("Alexawürfel"),
 * USE_DEVELOP_REST_SERVICE("Entwicklerservice");
 * 
 * @param session
 * @param activeFlagList
 * @returns
 */
function setSessionFlags(session, activeFlagList) {
	session.attributes.flags = {};
	if (activeFlagList) {
		if (activeFlagList.indexOf("DO_NOT_WAIT_FOR_ANSWER") != -1) {
			session.attributes.flags.doNotWaitForAnswer = true;
		}
		if (activeFlagList.indexOf("SHOW_PREPARED_ADVENTURES") != -1) {
			session.attributes.flags.showPreparedAdventures = true;
		}
		if (activeFlagList.indexOf("SHORTEN_TEXTS") != -1) {
			session.attributes.flags.shortenTexts = true;
		}
		if (activeFlagList.indexOf("ALEXA_ROLLS_DICES") != -1) {
			session.attributes.flags.alexaRollsDices = true;
		}
		if (activeFlagList.indexOf("USE_DEVELOP_REST_SERVICE") != -1) {
			session.attributes.flags.useDevelopRestService = true;
		}
	}
}

function getSessionEndpoint(session) {
	if (session && session.attributes && session.attributes.flags && session.attributes.flags.useDevelopRestService) {
		return endpointDev;
	}
	return endpoint;
}

function setSessionGameId(session, gameId) {
	session.attributes.gameId = gameId;
}
function getSessionGameId(session) {
	return session.attributes.gameId;
}

function getGameId(intent) {
	return getGameIdLetter(intent, "?") + getGameIdNumber(intent, "?");
}


function getGameIdLetter(intent, defaultValue) {
	var letter = getFromIntent(intent, "gameid_letter", defaultValue);
	if (letter.length > 0) {
		letter = letter.substring(0,1);
	}
	return letter;
}

function getGameIdNumber(intent, defaultValue) {
	return getFromIntent(intent, "gameid_number", defaultValue);
}

function getFromIntent(intent, attribute_name, defaultValue) {
	if (!intent.slots) {
		return defaultValue;
	}
	var result = intent.slots[attribute_name];
	if (!result || !result.value) {
		return defaultValue;
	}
	return result.value;
}


function sendCommand(session, gameId, cmd, param1, param2, callback) {

	var result = "";
	
	var query = querystring.stringify({
		"gameId": gameId,
		"cmd": cmd,
		"param1": param1,
		"param2": param2
	});
    var url = getSessionEndpoint(session) + "?" + query;
    console.log('CALL: ' + url);
    
    var urlObj = URL.parse(url);
    var options = {
    		protocol: urlObj.protocol,
    		host: urlObj.hostname,
    	    port: urlObj.port,
    	    path: urlObj.path,
    		auth: authUsername+':'+authPassword
    };
    
    http.get(options, function (res) {
        var responseString = '';
        if (res.statusCode != 200) {
            console.log("ERROR HTTP STATUS " + res.statusCode);
            result = {code:"E_CONNECT", errmsg: "h.t.t.p. Status "+res.statusCode};
            callback(result);
        }
        res.on('data', function (data) {
        	responseString += data;
        });
        res.on('end', function () {
            console.log("get-end: " + responseString);
            var responseObject;
            try {
                responseObject = JSON.parse(responseString);
            } catch(e) {
                console.log("E_CONNECT INVALID JSON-FORMAT: " + e.message);
                responseObject = {
                		code: "E_CONNECT", errmsg: "Die Serverantwort ist nicht valide."
                };
            }
            callback(responseObject);
            
        });
    }).on('error', function (e) {
        console.log("E_CONNECT: " + e.message);
        result = {
        		code: "E_CONNECT", errmsg: e.message
        };
        callback(result);
    });
}



//initialize tests
exports.initTests = function (url, param) {
	endpoint = url;
	sendCommand([], "?", "initTests", param, "", function callbackFunc(result) {
		console.log(result);
	});
}



