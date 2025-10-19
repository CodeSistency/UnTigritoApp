# Auth Backend Integration Spec

## Overview
Integration of the authentication module with the backend API endpoints for UnTigrito Android application.

## Requirements Summary
- **Priority**: All authentication endpoints (Login/Register, Email verification, OTP, ID verification, Google OAuth)
- **Token Storage**: SharedPreferences (non-encrypted)
- **Token Refresh**: Automatic refresh implementation required
- **Validation Level**: Comprehensive frontend validation
- **Phone Format**: Venezuelan format (04120386216)
- **Password Validation**: Same criteria as backend
- **Cedula Validation**: 8 digits format

## Technical Requirements

### Authentication Endpoints
1. **Login/Register Basic**
   - POST /api/auth/login
   - POST /api/auth/register
   - POST /api/auth/logout
   - POST /api/auth/refresh

2. **Email Verification**
   - POST /api/auth/verify-email
   - POST /api/auth/forgot-password
   - POST /api/auth/reset-password

3. **OTP Phone Verification**
   - POST /api/user/send-otp
   - POST /api/user/verify-otp
   - POST /api/user/verify-phone

4. **ID Verification**
   - POST /api/user/verify-id

5. **Google OAuth**
   - POST /api/auth/google

### Token Management
- Storage: SharedPreferences
- Automatic refresh: Implemented
- JWT payload structure support
- Refresh token handling

### Validation Requirements
- Phone: Venezuelan format (04120386216)
- Password: Backend criteria (8+ chars, uppercase, lowercase, number, special char)
- Cedula: 8 digits format
- Email: Standard email validation

## Implementation Scope
- Repository layer integration
- Use case implementations
- Data source implementations
- Token management utilities
- Validation utilities
- Error handling
- Network configuration

## Success Criteria
- All authentication endpoints integrated
- Token management working
- Validation working correctly
- Error handling implemented
- Ready for UI integration
