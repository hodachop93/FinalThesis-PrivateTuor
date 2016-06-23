import ExtendableError from 'es6-error'

export default class UserException extends ExtendableError {

    constructor(message = 'Default message', errorCode = 6000, errorDevMessage = 'Technical Exception', data = null) {
        super(message, errorCode);
        this.errorCode = errorCode;
        this.errorDevMessage = errorDevMessage;
        this.data = data;
    }
}