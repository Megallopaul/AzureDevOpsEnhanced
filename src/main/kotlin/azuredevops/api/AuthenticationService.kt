package azuredevops.api

import azuredevops.result.ApiResult

/**
 * User identity information
 */
data class UserIdentity(
    val id: String,
    val displayName: String,
    val uniqueName: String,
    val email: String? = null,
)

/**
 * Device code for OAuth authentication flow
 */
data class DeviceCode(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Long,
    val interval: Long,
)

/**
 * Service interface for authentication operations.
 *
 * Responsible for:
 * - Validating PAT tokens
 * - OAuth device code flow
 * - Token refresh
 * - Getting current user identity
 */
interface AuthenticationService {
    /**
     * Validates a Personal Access Token (PAT).
     *
     * @param token The PAT to validate
     * @return Success with UserIdentity if valid, Error with ApiError otherwise
     */
    fun validateToken(token: String): ApiResult<UserIdentity>

    /**
     * Requests a device code for OAuth authentication.
     *
     * @return Success with DeviceCode if successful, Error otherwise
     */
    fun requestDeviceCode(): ApiResult<DeviceCode>

    /**
     * Refreshes an OAuth token using a device code.
     *
     * @param deviceCode The device code to refresh
     * @return Success with new token if successful, Error otherwise
     */
    fun refreshDeviceCode(deviceCode: DeviceCode): ApiResult<String>

    /**
     * Gets the current authenticated user identity.
     *
     * @return Success with UserIdentity if authenticated, Error otherwise
     */
    fun getCurrentUser(): ApiResult<UserIdentity>

    /**
     * Clears all stored authentication credentials.
     */
    fun clearCredentials()
}
