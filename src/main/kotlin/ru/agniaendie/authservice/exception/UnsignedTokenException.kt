package ru.agniaendie.authservice.exception

/**
 *  The class {@code UnsignedTokenException} indicates a JwtToken be valid but was signed with another key
 * @author AgniaEndie
 */

class UnsignedTokenException(override var message:String) : Exception()