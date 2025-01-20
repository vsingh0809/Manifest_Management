using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Security.Cryptography;

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ManifestPortal1.Entities.EFCore;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.DataAccess.Repository.Contracts;
using ManifestPortal1.DataAccess.Helper;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;

namespace ManifestPortal1.Business.Class
{
    public class CrewService : ICrewService
    {
        private readonly ICrewRepository operatorRepository;
        private readonly JwtConfig _jwtConfig;  //jwt service obj.

        public CrewService(ICrewRepository operatorRepository, JwtConfig jwtConfig)
        {
            this.operatorRepository = operatorRepository;
            _jwtConfig = jwtConfig;
        }

        public IEnumerable<Crewoperator> GetAllOperators()
        {
            return operatorRepository.GetAllOperators();
        }

        public Crewoperator GetOperatorById(int id)
        {
            return operatorRepository.GetOperatorById(id);
        }

        //newely add code
        public Crewoperator GetOperatorByEmail(string email)
        {
            return operatorRepository.GetOperatorByCrewoperatorEmail(email);
        }
        public Crewoperator GetOperatoByUserName(string userName)
        {
            return operatorRepository.GetCrewOperatorByUserName(userName);
        }

        public Crewoperator UpdateOperator(int id, UpdateCrewoperatorDTO newOperator)
        {
            var operatorEntity = operatorRepository.GetOperatorById(id);
            if (operatorEntity == null)
            {
                return null;
            }

            operatorEntity.FirstName = newOperator.FirstName;
            operatorEntity.LastName = newOperator.LastName;
            operatorEntity.ContactNumber = newOperator.ContactNumber;
            operatorEntity.Dob = newOperator.Dob;
            operatorEntity.Gender = newOperator.Gender;
            //operatorEntity.Gender = newOperator.Gender;//type coversion problem is here
            //operatorEntity.CrewoperatorImage = newOperator.CrewoperatorImage;
            //operatorEntity.CrewoperatorUsername = newOperator.CrewoperatorUsername;

            return operatorRepository.UpdateOperator(operatorEntity);
        }


        //-------------------------------------------------------------------

        //Login user JWT
        /////service layer API

        public async Task<string> LoginCrewoperatorAsync(LoginDTO loginDto)
        {
            if (loginDto == null || string.IsNullOrEmpty(loginDto.email) || string.IsNullOrEmpty(loginDto.password))
            {
                throw new ArgumentException("Email and Password are required.");
            }

            // Repository method to check if the operator exists
            var crewOperator = await operatorRepository.GetOperatorByCrewoperatorEmailAsync(loginDto.email);
            if (crewOperator == null)
            {
                throw new KeyNotFoundException("Email not found.");
            }

            // Check if the password matches (assuming hashed password in the database)
            bool isPasswordValid = VerifyPassword(loginDto.password, crewOperator.Password);
            if (!isPasswordValid)
            {
                throw new UnauthorizedAccessException("Invalid password.");
            }

            // Generate and return the JWT token
            var token = await GenerateTokenAsync(crewOperator.CrewUsername, crewOperator.Email, crewOperator.CrewId);
            return token;
        }

        //private bool VerifyPassword(string inputPassword, string storedPassword)
        //{
        //    // Assuming a method for password verification (hash comparison)
        //    return inputPassword == storedPassword; // Simple comparison (should use hashing in real scenarios)
        //}

        public async Task<string> GenerateTokenAsync(string username, string email, int crewId)
        {
            return await Task.FromResult(_jwtConfig.GenerateToken(username, email, crewId));
        }


        ////----------------------------------------------------------

        // Add operator
        public async Task<Crewoperator> RegisterCrewOperator(AddCrewoperatorDTO addOperator)
        {
            try
            {
                // Check if CrewoperatorUsername already exists
                var existingUser = GetOperatoByUserName(addOperator.CrewUsername);
                if (existingUser != null)
                {
                    throw new ArgumentException($"The CrewoperatorUsername '{addOperator.CrewUsername}' is already taken.");
                }

                // Check if Email already exists
                existingUser = GetOperatorByEmail(addOperator.Email);
                if (existingUser != null)
                {
                    throw new ArgumentException($"This Email '{addOperator.Email}' is already registered. Please Log In.");
                }

                // Hash the password
                var passwordHash = HashPassword(addOperator.Password);

                // Create Crewoperator entity
                var operatorEntity = new Crewoperator
                {
                    FirstName = addOperator.FirstName,
                    LastName = addOperator.LastName,
                    Salutation = addOperator.Salutation,
                    Email = addOperator.Email,
                    ContactNumber = addOperator.ContactNumber,
                    Password = passwordHash,
                    Dob = addOperator.Dob,
                    Gender = addOperator.Gender,
                    CrewUsername = addOperator.CrewUsername,
                };

                // Save to repository
                return await operatorRepository.RegisterCrewOperator(operatorEntity);
            }
            catch (ArgumentException ex)
            {
                // Log error (if logging is set up)
                // Return meaningful error message to the caller
                throw new InvalidOperationException(ex.Message);
            }
            catch (Exception ex)
            {
                // Handle unexpected exceptions
                throw new InvalidOperationException("An error occurred while registering the crew operator.", ex);
            }
        }



        // Password Hashing
        private string HashPassword(string password)
        {
            using (var sha256 = SHA256.Create())
            {
                var bytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
                return Convert.ToBase64String(bytes);
            }
        }
        //Converting Hash to human redable format 
        private bool VerifyPassword(string password, string hashedPassword)
        {
            using (var sha256 = SHA256.Create())
            {
                var bytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
                var computedHash = Convert.ToBase64String(bytes);
                return computedHash == hashedPassword;
            }
        }


        public bool VerifyOldPassword(string oldPassword, int id)
        {
            // Implement the password verification logic with Id
            // Example: Retrieve the stored password hash using the Id from the database
            var operatorEntity = operatorRepository.GetOperatorById(id); // Retrieve the stored password for the given Id

            if (operatorEntity == null)
            {
                return false; // Id not found or invalid
            }
            oldPassword = HashPassword(oldPassword);
            return operatorEntity.Password == oldPassword; // This function checks if the oldPassword matches the stored hash
        }


        //public Crewoperator UpdateEmail(int id, string newEmail)
        //{
        //    var operatorEntity = operatorRepository.GetOperatorById(id);
        //    if (operatorEntity == null)
        //    {
        //        return null;
        //    }

        //    operatorEntity.Email = newEmail;
        //    return operatorRepository.UpdateOperator(operatorEntity);
        //}


        public Crewoperator UpdateEmail(int id, string newEmail)
        {
            // Step 1: Retrieve the operator entity by id
            var operatorEntity = operatorRepository.GetOperatorById(id);

            if (operatorEntity == null)
            {
                // Return null if operator does not exist
                return null;
            }

            // Step 2: Check if the new email already exists in the system (excluding the current operator)
            var existingOperator = operatorRepository.GetOperatorByCrewoperatorEmail(newEmail);

            if (existingOperator != null && existingOperator.CrewId != id)
            {
                // If the email already exists and it's not the same operator, return null or handle as needed
                return null;  
            }

            // Step 3: Update the operator's email if the new email is valid
            operatorEntity.Email = newEmail;

            // Step 4: Save the updated operator in the repository
            return operatorRepository.UpdateOperator(operatorEntity);
        }

        public Crewoperator UpdatePassword(int id, string newPassword)
        {
            var operatorEntity = operatorRepository.GetOperatorById(id);
            if (operatorEntity == null)
            {
                return null;
            }
            newPassword = HashPassword(newPassword);

            operatorEntity.Password = newPassword;
            return operatorRepository.UpdateOperator(operatorEntity);
        }

        public Crewoperator UpdateImage(int id, byte[] newImage)
        {
            var operatorEntity = operatorRepository.GetOperatorById(id);
            if (operatorEntity == null)
            {
                return null;
            }

            operatorEntity.CrewImage = newImage;
            return operatorRepository.UpdateOperator(operatorEntity);
        }


        public async Task<ServiceResponseDTO> UpdateCrewImageAsync(int crewId, UpdateCrewImageRequestDTO request)
        {
            try
            {
                var crewOperator = await operatorRepository.GetCrewOperatorByIdAsync(crewId);

                if (crewOperator == null)
                {
                    return new ServiceResponseDTO { Success = false, StatusCode = 404, Message = "Crew operator not found." };
                }

                // Decode Base64 string to binary
                var imageBytes = Convert.FromBase64String(request.ImageBase64);

                // Update the CrewImage field
                crewOperator.CrewImage = imageBytes;

                // Save changes
                await operatorRepository.UpdateCrewOperatorAsync(crewOperator);

                return new ServiceResponseDTO { Success = true, Message = "Crew image updated successfully." };
            }
            catch (FormatException)
            {
                return new ServiceResponseDTO { Success = false, StatusCode = 400, Message = "Invalid Base64 string." };
            }
            catch (Exception ex)
            {
                return new ServiceResponseDTO { Success = false, StatusCode = 500, Message = $"Internal server error: {ex.Message}" };
            }
        }





    }

}

